package org.lunker.new_proxy.server;

import com.google.gson.JsonObject;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.rest.HttpService;
import org.lunker.new_proxy.stub.AbstractSIPHandler;
import org.lunker.new_proxy.util.AuthUtil;
import org.lunker.new_proxy.util.Registrar;
import org.lunker.new_proxy.util.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.MessageFactory;


/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class SIPHandler extends ChannelInboundHandlerAdapter implements AbstractSIPHandler{
    private Logger logger= LoggerFactory.getLogger(SIPHandler.class);

    private ChannelHandlerContext ctx=null;
    private javax.sip.SipFactory sipFactory=null;
    private HeaderFactory headerFactory=null;
    private MessageFactory messageFactory=null;
    private Registrar registrar=null;

    public SIPHandler() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Channel Active!!!!!!!!!!!!!!!!!!!!!!!!!!");
        this.ctx=ctx;
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
        logger.info("In SIPHandler");
        logger.info("[RECEIVED]:\n" + ((SIPMessage) msg).toString());

        SIPMessage sipMessage=(SIPMessage) msg;

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
            String password="";
            String ipPhonePassword="";

            aor=registerRequest.getFrom().getAddress().getURI().toString().split(":")[1];
            account=aor.split("@")[0];
            domain=aor.split("@")[1];

            authorization=authHeader.toString();

            // TODO(lunker): get password from rest
            HttpService httpService=new HttpService();
            try{
                JsonObject response=httpService.get("/ims/users/"+aor+"/password", JsonObject.class);
                String status=response.getAsJsonObject("header").get("status").getAsString();
                if (!status.equals("error")) {
                    password = response.get("body").getAsJsonObject().get("password").getAsString();
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

                // store registration info
                Registration registration=new Registration(this.ctx, aor,"","");
                registrar.register(aor, registration);
            }
            else{
                logger.warn("REGISTER Fail");
            }
        }

        // fire response
        ctx.fireChannelRead(sipResponse.toString());
    }

    @Override
    public void handleInvite(SIPRequest inviteRequest) {
        logger.info("handleInvite");
        SIPResponse sipResponse=null;
        String toAor="";

        toAor=inviteRequest.getTo().getAddress().getURI().toString().split(":")[1];

        Registration registration=registrar.get(toAor);

        if(registration==null){
            // error

            sipResponse=inviteRequest.createResponse(SIPResponse.TEMPORARILY_UNAVAILABLE, "User is not registered");
            this.ctx.fireChannelRead(sipResponse.toString());
        }
        else{
//            sipResponse=inviteRequest.createResponse(SIPResponse.OK);
//            this.ctx.fireChannelRead(sipResponse.toString());
            registration.getCtx().fireChannelRead(inviteRequest.toString());
        }
    }

    @Override
    public void handleCancel(SIPRequest request) {

    }

    @Override
    public void handleAck(SIPRequest ackRequest) {
        logger.info("handleAck");

        String targetAor="";
        Registration registration=null;

        targetAor=ackRequest.getTo().getAddress().getURI().toString().split(":")[1];
        registration=registrar.get(targetAor);

        if(registration==null){
            // TODO: error
            // ?
        }
        else{
            // forward ack
            registration.getCtx().fireChannelRead(ackRequest.toString());
        }
    }

    @Override
    public void handleBye(SIPRequest byeRequest) {
        logger.info("handleBye");

        String targetAor="";
        Registration registration=null;

        targetAor=byeRequest.getTo().getAddress().getURI().toString().split(":")[1];
        registration=registrar.get(targetAor);

        if(registration==null){
            // TODO: error
        }
        else{
            SIPResponse sipResponse=byeRequest.createResponse(SIPResponse.OK);

            registration.getCtx().fireChannelRead(byeRequest.toString());
            this.ctx.fireChannelRead(sipResponse.toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught!: " + cause.getMessage());
        cause.printStackTrace();
//        super.exceptionCaught(ctx, cause);
    }
}
