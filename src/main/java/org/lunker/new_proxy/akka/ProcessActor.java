package org.lunker.new_proxy.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipRequest;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipResponse;
import org.lunker.new_proxy.util.AuthUtil;
import org.lunker.new_proxy.util.Registrar;
import org.lunker.new_proxy.util.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.MessageFactory;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongqlee on 2018. 3. 31..
 */
public class ProcessActor extends AbstractActor {
    private Logger logger= LoggerFactory.getLogger(ProcessActor.class);
    private ActorRef postProcessActorRef=null;
    private ChannelHandlerContext currentCtx=null;

    private javax.sip.SipFactory sipFactory=null;
    private HeaderFactory headerFactory=null;
    private MessageFactory messageFactory=null;
    private Registrar registrar=null;

    static public Props props(ActorRef postProcessActorRef) {
        return Props.create(ProcessActor.class, () -> new ProcessActor(postProcessActorRef));
    }

    public ProcessActor(ActorRef postProcessActorRef) {
        this.postProcessActorRef = postProcessActorRef;

        try{
            this.sipFactory=javax.sip.SipFactory.getInstance();
            this.headerFactory=sipFactory.createHeaderFactory();
            this.registrar= Registrar.getInstance();
            this.messageFactory=sipFactory.createMessageFactory();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public ProcessActor(GeneralSipMessage generalSipMessage, ActorRef postProcessActorRef) {
        this.postProcessActorRef=postProcessActorRef;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GeneralSipMessage.class, (generalSipMessage)->{

                    logger.info("In proessActor");

                    List<GeneralSipMessage> messageToBeSent=process(generalSipMessage);

                    messageToBeSent.stream().forEach(message -> {
                        postProcessActorRef.tell(message, getSelf());
                    });
                })
                .build();
    }


    public List<GeneralSipMessage> process(GeneralSipMessage generalSipMessage){
        GeneralSipMessage sipMessage=(GeneralSipMessage) generalSipMessage;
        GeneralSipMessage targetMessage=null;

        List<GeneralSipMessage> results=new ArrayList<>();

        this.currentCtx=sipMessage.getSipSession().getCtx();

        // test SipSession
        sipMessage.getSipSession().setAttribute("hi",123);

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

        results.add(targetMessage);

        return results;
    }

    public GeneralSipMessage handleResponse(GeneralSipMessage response){
        response=(GeneralSipResponse) response;

        int statusCode=((GeneralSipResponse) response).getStatusCode();

        String method=response.getMethod();

        if(method.equals("INVITE") && statusCode== SIPResponse.OK){
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
//            this.targetCtx.fireChannelRead(response.toString());
        }

        return response;
    }

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
//                jedisConnection.set(userKey, gson.toJson(registration));
            }
            else{
                logger.warn("REGISTER Fail");
            }
        }

        // fire response
//        this.currentCtx.fireChannelRead(sipResponse);
        return sipResponse;
    }

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

    public GeneralSipMessage handleCancel(GeneralSipMessage cancelRequest) {

        return cancelRequest;
    }

    public GeneralSipMessage handleAck(GeneralSipMessage ackRequest) {
        logger.info("handleAck");

        return ackRequest;
    }

    public GeneralSipMessage handleBye(GeneralSipMessage byeRequest) {
        logger.info("handleBye");

        return byeRequest;
    }
}
