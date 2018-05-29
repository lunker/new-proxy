package org.lunker.proxy.sip.process;

import com.google.gson.Gson;
import gov.nist.javax.sip.message.SIPResponse;
import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipRequest;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipResponse;
import org.lunker.new_proxy.stub.AbstractSIPHandler;
import org.lunker.proxy.core.Message;
import org.lunker.proxy.core.ProcessState;
import org.lunker.proxy.core.ProxyHandler;
import org.lunker.proxy.model.RemoteAddress;
import org.lunker.proxy.registrar.Registrar;
import org.lunker.proxy.registrar.Registration;
import org.lunker.proxy.sip.process.request.ProxyRequestHandler;
import org.lunker.proxy.sip.process.response.ProxyResponseHandler;
import org.lunker.proxy.sip.process.stateless.ProxyStatelessRequestHandler;
import org.lunker.proxy.sip.process.stateless.ProxyStatelessResponseHandler;
import org.lunker.proxy.util.AuthUtil;
import org.lunker.proxy.util.JedisConnection;
import org.lunker.proxy.util.ProxyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.sip.address.AddressFactory;
import javax.sip.address.URI;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.MessageFactory;

/**
 * Created by dongqlee on 2018. 5. 15..
 */
public class ProxyInHandler implements AbstractSIPHandler, ProxyHandler {
    private Logger logger= LoggerFactory.getLogger(ProxyInHandler.class);

    private javax.sip.SipFactory sipFactory=null;
    private AddressFactory addressFactory=null;
    private HeaderFactory headerFactory=null;
    private MessageFactory messageFactory=null;
    private Registrar registrar=null;
    private JedisConnection jedisConnection=null;
    private Gson gson=null;

    private ProxyRequestHandler proxyRequestHandler=null;
    private ProxyResponseHandler proxyResponseHandler=null;
    private ProxyStatelessRequestHandler proxyStatelessRequestHandler=null;
    private ProxyStatelessResponseHandler proxyStatelessResponseHandler=null;

    public ProxyInHandler(ServerInfo serverInfo) {
        jedisConnection=JedisConnection.getInstance();
        gson=new Gson();

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

        proxyRequestHandler=new ProxyRequestHandler();
        proxyResponseHandler=new ProxyResponseHandler();

        proxyStatelessRequestHandler=new ProxyStatelessRequestHandler(serverInfo);
        proxyStatelessResponseHandler=new ProxyStatelessResponseHandler();
    }

    @Override
    public Message handle(Message message) {
        if(message.getProcessState() != ProcessState.IN)
            return message;

        DefaultSipMessage originalMessage=null;
        Mono<Message> messageProcessMono=null;

        originalMessage=message.getOriginalMessage();

        if(originalMessage instanceof ProxySipRequest){
            messageProcessMono=Mono.just(message)
                    .map(proxyStatelessRequestHandler::handle);
        }
        else if(originalMessage instanceof ProxySipResponse){
            messageProcessMono=Mono.just(message)
                    .map(proxyStatelessResponseHandler::handle);
        }

        messageProcessMono.subscribeOn(Schedulers.single());
        messageProcessMono.block();

        if(message.getValidation().isValidate()){
            message.setProcessState(ProcessState.POST);
        }

        return message;
    }

    /**
     * Remove top Via
     * @param response
     * @return
     */
    public DefaultSipMessage handleResponse(DefaultSipMessage response){
        return response;
    }

    @Override
    public DefaultSipMessage handleInvite(DefaultSipMessage inviteRequest) {
        logger.info("handleInvite");

        ProxySipRequest proxyInviteRequest=null;
        proxyInviteRequest=(ProxySipRequest) inviteRequest;

        String userKey=inviteRequest.getTo().getAddress().getURI().toString().split(":")[1];
        Registration registration=null;

        registration=this.registrar.getRegistration(userKey);
        String displayName=inviteRequest.getTo().getAddress().getDisplayName();

        try{
            URI requestURI=this.addressFactory.createURI("sip:" + displayName + "@" + registration.getRemoteAddress() + ":" + registration.getRemotePort());
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
        Registration registration=null;

        registration=this.registrar.getRegistration(aor);

        String displayName=ackRequest.getTo().getAddress().getDisplayName();
        try{
            URI requestURI=this.addressFactory.createURI("sip:" + displayName + "@" + registration.getRemoteAddress() + ":" + registration.getRemotePort());
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
        Registration registration=null;

        registration=this.registrar.getRegistration(aor);

        String displayName=byeRequest.getTo().getAddress().getDisplayName();
        try{
            URI requestURI=this.addressFactory.createURI("sip:" + displayName + "@" + registration.getRemoteAddress() + ":" + registration.getRemotePort());
            ((ProxySipRequest)byeRequest).setRequestURI(requestURI);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return byeRequest;
    }

    @Override
    public DefaultSipMessage handleRegister(DefaultSipMessage registerRequest) {
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
                sipResponse=((ProxySipRequest) registerRequest).createResponse(SIPResponse.OK);

                //TODO: get first via received & rport
                RemoteAddress clientRemoteAddress=ProxyHelper.getClientRemoteAddress(registerRequest);

                Registration registration=new Registration(userKey, aor,account, domain, clientRemoteAddress.getHost(), clientRemoteAddress.getPort());

                registrar.register(userKey, registration);
//                registrar.register(userKey, registration, ctx);
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

}
