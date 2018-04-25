package org.lunker.new_proxy.sip.handler;

import com.google.gson.Gson;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.sip.context.ProxyContext;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipRequest;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipResponse;
import org.lunker.new_proxy.stub.AbstractSIPHandler;
import org.lunker.new_proxy.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.MessageFactory;
import java.net.InetSocketAddress;


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
    private ProxyContext proxyContext=null;

    public SIPProcessor() {
        jedisConnection=JedisConnection.getInstance();
        gson=new Gson();
        proxyContext=ProxyContext.getInstance();
    }

    @Override
    public void channelActive(ChannelHandlerContext currentCtx) throws Exception {
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
        logger.info("[RECEIVED]:\n" + ((GeneralSipMessage) msg).toString());

        GeneralSipMessage sipMessage=(GeneralSipMessage) msg;
        GeneralSipMessage targetMessage=null;

        // test SipSession
//        sipMessage.getSipSession().setAttribute("hi",123);

        if(sipMessage instanceof GeneralSipRequest){
            String method=sipMessage.getMethod();
            if(method.equals(SIPRequest.REGISTER))
                targetMessage=this.handleRegister(sipMessage);
            else if (method.equals(SIPRequest.INVITE))
                targetMessage=this.handleInvite(sipMessage);
            else if(method.equals(SIPRequest.ACK))
                targetMessage=this.handleAck(sipMessage);
            else if(method.equals(SIPRequest.BYE))
                targetMessage=this.handleBye(sipMessage);
        }
        else if(sipMessage instanceof GeneralSipResponse){
            targetMessage=handleResponse(sipMessage);
        }

        this.currentCtx.fireChannelRead(targetMessage);
    }

    public GeneralSipMessage handleResponse(GeneralSipMessage response){
        response=(GeneralSipResponse) response;

        int statusCode=((GeneralSipResponse) response).getStatusCode();

        String method=response.getMethod();

        if(method.equals("INVITE") && statusCode==SIPResponse.OK){
            GeneralSipRequest invite=(GeneralSipRequest) response.getSipSession().getAttribute("invite");
            System.out.println("asdf");

            GeneralSipResponse generalSipResponse=invite.createResponse(statusCode);


        }
        else if(method.equals("INVITE") && statusCode==SIPResponse.RINGING){
            response=null;
        }
        else if(method.equals("BYE") && statusCode==SIPResponse.OK){
            String targetAor="";
            ChannelHandlerContext targetCtx=null;

            targetAor=response.getFrom().getAddress().getURI().toString().split(":")[1];

            targetCtx=registrar.getCtx(targetAor);
            targetCtx.fireChannelRead(response.toString());
        }
        else {
            logger.warn("Not implemented call logic . . .");
            this.targetCtx.fireChannelRead(response.toString());
        }

        return response;
    }

    @Override
    public GeneralSipMessage handleRegister(GeneralSipMessage registerRequest) {
//        Optional<Header> authorization = Optional.ofNullable(registerRequest.getHeader("Authorization"));
        registerRequest=(GeneralSipRequest) registerRequest;
        Header authHeader= registerRequest.getHeader("Authorization");
        String authorization="";
        GeneralSipResponse sipResponse=null;

        if(authHeader==null){
            // non-auth
            sipResponse=((GeneralSipRequest) registerRequest).createResponse(SIPResponse.UNAUTHORIZED);
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

//            String userAgent= SIPHeaderParser.getUserAent(registerRequest);

            userKey=aor;// 1단계에서는 OPMD 지원 고려 안하는걸로.

            authorization=authHeader.toString();


            // TODO(lunker): get password from rest

            /*
            HttpService httpService=HttpService.getInstance();

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
            */

            AuthUtil authUtil=new AuthUtil(authorization);
            authUtil.setPassword(ipPhonePassword);

            if(authUtil.isEqualHA()){
                // Auth success
                logger.warn("REGISTER Success");
                sipResponse=((GeneralSipRequest) registerRequest).createResponse(SIPResponse.OK);

                // store to redis
                // store registration info in cache
                String remoteAddress="";
                int remotePort=0;

                remoteAddress=((InetSocketAddress)this.currentCtx.channel().remoteAddress()).getHostString();
                remotePort=((InetSocketAddress)this.currentCtx.channel().remoteAddress()).getPort();
                Registration registration=new Registration(userKey, aor,account, domain, remoteAddress, remotePort);

                registrar.register(userKey, registration, this.currentCtx);
                jedisConnection.set(userKey, gson.toJson(registration));
            }
            else{
                logger.warn("REGISTER Fail");
            }
        }

        // fire response
//        this.currentCtx.fireChannelRead(sipResponse);
        return sipResponse;
    }

    @Override
    public GeneralSipMessage handleInvite(GeneralSipMessage inviteRequest) {
        logger.info("handleInvite");

        inviteRequest=(GeneralSipRequest) inviteRequest;
        GeneralSipResponse sipResponse=null;

        // 1) Create 180 Ringing to Caller
        sipResponse=((GeneralSipRequest) inviteRequest).createResponse(SIPResponse.RINGING);

        // TODO:: 1개의 로직에서 여러개의 SipMessage를 전송해야 한다. 이를 위해서 List<GeneralSipMessage> 로 return되도록 관련 로직 수정
        try{
            sipResponse.send();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        GeneralSipRequest forwardedRequest=null;

        forwardedRequest=inviteRequest.getSipSession().createRequest("INVITE");
        forwardedRequest.setContent(((GeneralSipRequest) inviteRequest).getContent(), "application/sdp");

        // 2) Create INVITE to Callee

        // store Invite for b2bua
        forwardedRequest.getSipSession().setAttribute("invite", forwardedRequest);

        return forwardedRequest;
    }

    @Override
    public GeneralSipMessage handleCancel(GeneralSipMessage cancelRequest) {

        return cancelRequest;
    }

    @Override
    public GeneralSipMessage handleAck(GeneralSipMessage ackRequest) {
        logger.info("handleAck");

        return ackRequest;
    }

    @Override
    public GeneralSipMessage handleBye(GeneralSipMessage byeRequest) {
        logger.info("handleBye");

        return byeRequest;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught!: " + cause.getMessage());
        cause.printStackTrace();
    }
}
