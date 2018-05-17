package proxy.core;

import com.google.gson.Gson;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.core.ProxyContext;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipRequest;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipResponse;
import org.lunker.new_proxy.stub.AbstractSIPHandler;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proxy.registrar.Registrar;
import proxy.registrar.Registration;
import proxy.sip.pre_process.ProxyPreHandler;
import proxy.util.AuthUtil;
import proxy.util.JedisConnection;

import javax.sip.address.AddressFactory;
import javax.sip.address.URI;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.MessageFactory;
import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 25..
 */
public class SipServletImpl implements AbstractSIPHandler, SipMessageHandler {
    private Logger logger= LoggerFactory.getLogger(SipServletImpl.class);

    private javax.sip.SipFactory sipFactory=null;
    private AddressFactory addressFactory=null;
    private HeaderFactory headerFactory=null;
    private MessageFactory messageFactory=null;
    private Registrar registrar=null;
    private JedisConnection jedisConnection=null;
    private Gson gson=null;
    private ProxyContext proxyContext=null;

    private ProxyPreHandler proxyPreHandler=null;

    public SipServletImpl() {
        jedisConnection=JedisConnection.getInstance();
        gson=new Gson();
        proxyContext= ProxyContext.getInstance();

        try{
            this.sipFactory=javax.sip.SipFactory.getInstance();
            this.headerFactory=sipFactory.createHeaderFactory();
            this.registrar= Registrar.getInstance();
            this.messageFactory=sipFactory.createMessageFactory();
            this.addressFactory=sipFactory.createAddressFactory();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        proxyPreHandler=new ProxyPreHandler();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Optional<DefaultSipMessage> maybeDefaultSipMessage) {

        maybeDefaultSipMessage.map((DefaultSipMessage)->{
            logger.info("[RECEIVED]:\n" + maybeDefaultSipMessage.get().toString());

            DefaultSipMessage targetMessage=null;

            if(DefaultSipMessage instanceof ProxySipRequest){
                String method=DefaultSipMessage.getMethod();
                if(method.equals(SIPRequest.REGISTER))
                    targetMessage=this.handleRegister(ctx, DefaultSipMessage);
                else if (method.equals(SIPRequest.INVITE))
                    targetMessage=this.handleInvite(DefaultSipMessage);
                else if(method.equals(SIPRequest.ACK))
                    targetMessage=this.handleAck(DefaultSipMessage);
                else if(method.equals(SIPRequest.BYE))
                    targetMessage=this.handleBye(DefaultSipMessage);
            }
            else if(DefaultSipMessage instanceof ProxySipResponse){
                targetMessage=handleResponse(DefaultSipMessage);
            }

            return targetMessage;
        }).ifPresent((targetMessage)->{
            try{

                targetMessage.send();

            }
            catch (Exception e){
                e.printStackTrace();
            }

        });
    }


    public DefaultSipMessage handleResponse(DefaultSipMessage response){
        response=(ProxySipResponse) response;

        int statusCode=((ProxySipResponse) response).getStatusCode();

        String method=response.getMethod();

        if(method.equals("INVITE") && statusCode== SIPResponse.OK){
//            ProxySipRequest invite=(ProxySipRequest) response.getSipSession().getAttribute("invite");
//            System.out.println("asdf");

//            ProxySipResponse generalSipResponse=invite.createResponse(statusCode);

            // forwarding 200 ok
        }
        else if(method.equals("INVITE") && statusCode==SIPResponse.RINGING){
//            response=null;
        }
        else if(method.equals("BYE") && statusCode==SIPResponse.OK){
            String targetAor="";
            ChannelHandlerContext targetCtx=null;

            targetAor=response.getFrom().getAddress().getURI().toString().split(":")[1];

            targetCtx=registrar.getCtx(targetAor);
        }
        else {
            logger.warn("Not implemented call logic . . .");
//            this.targetCtx.fireChannelRead(response.toString());
        }

        return response;
    }

    public DefaultSipMessage handleRegister(ChannelHandlerContext ctx, DefaultSipMessage registerRequest) {
//        Optional<Header> authorization = Optional.ofNullable(registerRequest.getHeader("Authorization"));
        registerRequest=(ProxySipRequest) registerRequest;
        Header authHeader= registerRequest.getHeader("Authorization");
        String authorization="";
        ProxySipResponse sipResponse=null;

        if(authHeader==null){
            // non-auth
            sipResponse=((ProxySipRequest) registerRequest).createResponse(SIPResponse.UNAUTHORIZED);
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
                sipResponse=((ProxySipRequest) registerRequest).createResponse(SIPResponse.OK);

                // store to redis
                // store registration info in cache
                String remoteAddress="";
                int remotePort=0;

                remoteAddress=((InetSocketAddress)ctx.channel().remoteAddress()).getHostString();
                remotePort=((InetSocketAddress)ctx.channel().remoteAddress()).getPort();
                Registration registration=new Registration(userKey, aor,account, domain, remoteAddress, remotePort);

                registrar.register(userKey, registration, ctx);

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
    public DefaultSipMessage handleInvite(DefaultSipMessage inviteRequest) {
        logger.info("handleInvite");


        ProxySipRequest proxyInviteRequest=null;
        proxyInviteRequest=(ProxySipRequest) inviteRequest;

        /*
        inviteRequest=(ProxySipRequest) inviteRequest;
        ProxySipResponse sipResponse=null;

        // 1) Create 180 Ringing to Caller
        sipResponse=((ProxySipRequest) inviteRequest).createResponse(SIPResponse.RINGING);

        // TODO:: 1개의 로직에서 여러개의 SipMessage를 전송해야 한다. 이를 위해서 List<DefaultSipMessage> 로 return되도록 관련 로직 수정
        try{
            sipResponse.send();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        ProxySipRequest forwardedRequest=null;

        forwardedRequest=inviteRequest.getSipSession().createRequest("INVITE");
        forwardedRequest.setContent(((ProxySipRequest) inviteRequest).getContent(), "application/sdp");

        // 2) Create INVITE to Callee

        String userKey=inviteRequest.getTo().getAddress().getURI().toString().split(":")[1];
        ChannelHandlerContext targetCtx=this.registrar.getCtx(userKey);
        String remoteHost="";
        int remotePort=0;


        remoteHost=((InetSocketAddress)targetCtx.channel().remoteAddress()).getHostString();
        remotePort=((InetSocketAddress)targetCtx.channel().remoteAddress()).getPort();

        String displayName=inviteRequest.getTo().getAddress().getDisplayName();
        try{
            URI requestURI=this.addressFactory.createURI("sip:" + displayName + "@" + remoteHost + ":" + remotePort);
            forwardedRequest.setRequestURI(requestURI);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // store Invite for b2bua
        forwardedRequest.getSipSession().setAttribute("invite", forwardedRequest);

        return forwardedRequest;

        return inviteRequest;
        */

        String userKey=inviteRequest.getTo().getAddress().getURI().toString().split(":")[1];
        ChannelHandlerContext targetCtx=this.registrar.getCtx(userKey);
        String remoteHost="";
        int remotePort=0;


        remoteHost=((InetSocketAddress)targetCtx.channel().remoteAddress()).getHostString();
        remotePort=((InetSocketAddress)targetCtx.channel().remoteAddress()).getPort();

        String displayName=inviteRequest.getTo().getAddress().getDisplayName();
        try{
            URI requestURI=this.addressFactory.createURI("sip:" + displayName + "@" + remoteHost + ":" + remotePort);
            proxyInviteRequest.setRequestURI(requestURI);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return proxyInviteRequest;
    }

    @Override
    public DefaultSipMessage handleCancel(DefaultSipMessage cancelRequest) {

        return cancelRequest;
    }

    @Override
    public DefaultSipMessage handleAck(DefaultSipMessage ackRequest) {
        logger.info("handleAck");

        // send ack to callee
        String aor=ackRequest.getTo().getAddress().getURI().toString().split(":")[1];
        ChannelHandlerContext targetCtx=this.registrar.getCtx(aor);

        String remoteHost="";
        int remotePort=0;


        remoteHost=((InetSocketAddress)targetCtx.channel().remoteAddress()).getHostString();
        remotePort=((InetSocketAddress)targetCtx.channel().remoteAddress()).getPort();

        String displayName=ackRequest.getTo().getAddress().getDisplayName();
        try{
            URI requestURI=this.addressFactory.createURI("sip:" + displayName + "@" + remoteHost + ":" + remotePort);
            ((ProxySipRequest)ackRequest).setRequestURI(requestURI);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return ackRequest;
    }

    @Override
    public DefaultSipMessage handleBye(DefaultSipMessage byeRequest) {
        logger.info("handleBye");

        String aor=byeRequest.getTo().getAddress().getURI().toString().split(":")[1];
        ChannelHandlerContext targetCtx=this.registrar.getCtx(aor);

        String remoteHost="";
        int remotePort=0;


        remoteHost=((InetSocketAddress)targetCtx.channel().remoteAddress()).getHostString();
        remotePort=((InetSocketAddress)targetCtx.channel().remoteAddress()).getPort();

        String displayName=byeRequest.getTo().getAddress().getDisplayName();
        try{
            URI requestURI=this.addressFactory.createURI("sip:" + displayName + "@" + remoteHost + ":" + remotePort);
            ((ProxySipRequest)byeRequest).setRequestURI(requestURI);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return byeRequest;
    }

    @Override
    public DefaultSipMessage handleRegister(DefaultSipMessage registerRequest) {
        return null;
    }
}
