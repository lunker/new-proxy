package org.lunker.new_proxy.sip.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.rest.HttpService;
import org.lunker.new_proxy.sip.SIPHeaderParser;
import org.lunker.new_proxy.stub.AbstractSIPHandler;
import org.lunker.new_proxy.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.MessageFactory;


/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class SIPProcessor extends ChannelInboundHandlerAdapter implements AbstractSIPHandler{
    private Logger logger= LoggerFactory.getLogger(SIPProcessor.class);

    private ChannelHandlerContext currentCtx=null;
    private ChannelHandlerContext targetCtx=null;
    private javax.sip.SipFactory sipFactory=null;
    private HeaderFactory headerFactory=null;
    private MessageFactory messageFactory=null;
    private Registrar registrar=null;
    private JedisConnection jedisConnection=null;
    private Gson gson=null;

    public SIPProcessor() {
        jedisConnection=JedisConnection.getInstance();
        gson=new Gson();
    }

    @Override
    public void channelActive(ChannelHandlerContext currentCtx) throws Exception {
        logger.info("Channel Active!!!!!!!!!!!!!!!!!!!!!!!!!!");

        this.currentCtx=currentCtx;

        try{
            this.sipFactory=javax.sip.SipFactory.getInstance();
            this.headerFactory=sipFactory.createHeaderFactory();
            this.registrar=Registrar.getInstance();
            this.messageFactory=sipFactory.createMessageFactory();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("In SIPProcessor");
        logger.info("[RECEIVED]:\n" + ((SIPMessage) msg).toString());

        SIPMessage sipMessage=(SIPMessage) msg;



        // find target context
        this.targetCtx=ProxyUtil.getTargetCtx(sipMessage);

        if(sipMessage instanceof SIPRequest){
            String method=((SIPRequest) sipMessage).getMethod();
            if(method.equals(SIPRequest.REGISTER))
                this.handleRegister((SIPRequest) sipMessage);
            else if (method.equals(SIPRequest.INVITE))
                this.handleInvite((SIPRequest) sipMessage);
            else if(method.equals(SIPRequest.ACK))
                this.handleAck((SIPRequest) sipMessage);
            else if(method.equals(SIPRequest.BYE))
                this.handleBye((SIPRequest) sipMessage);
        }
        else if(sipMessage instanceof SIPResponse){
            handleResponse((SIPResponse) sipMessage);
        }
    }

    public void handleResponse(SIPResponse response){
        int statusCode=response.getStatusCode();

        String method=response.getCSeq().getMethod();

        if(method.equals("INVITE") && statusCode==SIPResponse.OK){

            // handle 200 ok with INVITE
            String targetAor="";
            String userKey="";

            ChannelHandlerContext targetCtx=null;

            targetAor=response.getFrom().getAddress().getURI().toString().split(":")[1];

            targetCtx=registrar.getCtx(targetAor);

            if(targetCtx==null){

            }
            else{
                targetCtx.fireChannelRead(response.toString());
            }
        }
        else {
            logger.warn("Not implemented call logic . . .");
        }
    }

    @Override
    public void handleRegister(SIPRequest registerRequest) {
//        Optional<Header> authorization = Optional.ofNullable(registerRequest.getHeader("Authorization"));
        Header authHeader= registerRequest.getHeader("Authorization");
        String authorization="";
        SIPResponse sipResponse=null;

        if(authHeader==null){
            // non-auth
            sipResponse=registerRequest.createResponse(SIPResponse.UNAUTHORIZED);
            String domain=registerRequest.getFrom().getAddress().getURI().toString().split("@")[1];

            try{
                WWWAuthenticateHeader wwwAuthenticateHeader=this.headerFactory.createWWWAuthenticateHeader("Digest");
                wwwAuthenticateHeader.setAlgorithm("MD5");
                wwwAuthenticateHeader.setQop("auth");
                wwwAuthenticateHeader.setNonce(AuthUtil.getNonce());
                wwwAuthenticateHeader.setRealm(domain);

                sipResponse.addHeader(wwwAuthenticateHeader);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            // do auth
            String aor="";
            String account="";
            String domain="";
            String userKey="";
            String ipPhonePassword="aaaaaa";

            aor=registerRequest.getFrom().getAddress().getURI().toString().split(":")[1];
            account=aor.split("@")[0];
            domain=aor.split("@")[1];

            String userAgent= SIPHeaderParser.getUserAent(registerRequest);

            userKey=aor+"_"+userAgent;

            authorization=authHeader.toString();

            // TODO(lunker): get password from rest
            HttpService httpService=new HttpService();
            try{
                JsonObject response=httpService.get("/ims/users/"+aor+"/password", JsonObject.class);
                String status=response.getAsJsonObject("header").get("status").getAsString();
                if (!status.equals("error")) {
                    ipPhonePassword = response.get("body").getAsJsonObject().get("telNoPassword").getAsString();
                }
            }
            catch (Exception e){
                e.printStackTrace();
                // return
            }

            AuthUtil authUtil=new AuthUtil(authorization);
            authUtil.setPassword(ipPhonePassword);

            if(authUtil.isEqualHA()){
                // Auth success
                logger.warn("REGISTER Success");
                sipResponse=registerRequest.createResponse(SIPResponse.OK);

                // store to redis
                // store registration info in cache
                Registration registration=new Registration(userKey, aor,account, domain);
                registrar.register(userKey, registration, this.currentCtx);
                jedisConnection.set(userKey, gson.toJson(registration));
            }
            else{
                logger.warn("REGISTER Fail");
            }
        }

        // fire response
        this.currentCtx.fireChannelRead(sipResponse.toString());
    }

    @Override
    public void handleInvite(SIPRequest inviteRequest) {
        logger.info("handleInvite");
        SIPResponse sipResponse=null;
        String toAor="";
        String userAgent="";
        String userKey="";

        toAor=inviteRequest.getTo().getAddress().getURI().toString().split(":")[1];
        userAgent=SIPHeaderParser.getUserAent(inviteRequest);
        userKey=toAor+"_"+userAgent;

        ChannelHandlerContext targetCtx=registrar.getCtx(userKey);

        if(targetCtx==null){
            // error

            sipResponse=inviteRequest.createResponse(SIPResponse.TEMPORARILY_UNAVAILABLE, "User is not registered");
            this.currentCtx.fireChannelRead(sipResponse.toString());
        }
        else{
//            sipResponse=inviteRequest.createResponse(SIPResponse.OK);
//            this.ctx.fireChannelRead(sipResponse.toString());


            targetCtx.fireChannelRead(inviteRequest.toString());

            // store request for testing sip-session
        }
    }

    @Override
    public void handleCancel(SIPRequest request) {

    }

    @Override
    public void handleAck(SIPRequest ackRequest) {
        logger.info("handleAck");

        String targetAor="";
        String userKey="";
        String userAgent="";

        Registration registration=null;
        ChannelHandlerContext targetCtx=null;

        targetAor=ackRequest.getTo().getAddress().getURI().toString().split(":")[1];
        userAgent=SIPHeaderParser.getUserAent(ackRequest);
        userKey=targetAor+"_"+userAgent;

//        registration=registrar.get(targetAor);
        targetCtx=registrar.getCtx(userKey);

        if(targetCtx==null){
            // TODO: error
            // ?
        }
        else{
            // forward ack
            targetCtx.fireChannelRead(ackRequest.toString());
        }
    }

    @Override
    public void handleBye(SIPRequest byeRequest) {
        logger.info("handleBye");

        String targetAor="";
        String userAgent="";
        String userKey="";

        Registration registration=null;
        ChannelHandlerContext targetCtx=null;

        targetAor=byeRequest.getTo().getAddress().getURI().toString().split(":")[1];
        userAgent=SIPHeaderParser.getUserAent(byeRequest);
        userKey=targetAor+"_"+userAgent;

        targetCtx=registrar.getCtx(userKey);

        if(registration==null){
            // TODO: error
        }
        else{
            SIPResponse sipResponse=byeRequest.createResponse(SIPResponse.OK);

            targetCtx.fireChannelRead(byeRequest.toString());
            this.currentCtx.fireChannelRead(sipResponse.toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught!: " + cause.getMessage());
        cause.printStackTrace();
//        super.exceptionCaught(ctx, cause);
    }
}
