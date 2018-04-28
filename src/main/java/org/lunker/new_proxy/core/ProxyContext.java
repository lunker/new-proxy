package org.lunker.new_proxy.core;

import gov.nist.javax.sip.message.SIPMessage;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.sip.session.SIPSessionManagerImpl;
import org.lunker.new_proxy.sip.session.sas.SipApplicationSessionKey;
import org.lunker.new_proxy.sip.session.ss.SipSessionKey;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipMessage;
import org.lunker.new_proxy.stub.session.sas.SipApplicationSession;
import org.lunker.new_proxy.stub.session.ss.SipSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class ProxyContext {

    private Logger logger= LoggerFactory.getLogger(ProxyContext.class);
    private static ProxyContext instance=null;
    private SIPSessionManagerImpl sipSessionManager=null;
    private ConcurrentHashMap<String, ChannelHandlerContext> clientMap=null;
//    private Registrar registrar=null;


    private ProxyContext() {
        this.sipSessionManager=new SIPSessionManagerImpl();
//        this.registrar=Registrar.getInstance();

        this.clientMap=new ConcurrentHashMap<>();
    }

    public static ProxyContext getInstance() {
        if (instance==null)
            instance=new ProxyContext();
        return instance;
    }

    public SipSession createOrGetSIPSession(ChannelHandlerContext ctx, ProxySipMessage proxySipMessage){
        return sipSessionManager.createOrGetSIPSession(ctx, proxySipMessage.getRawSipMessage());
    }

    public SipSession createOrGetSIPSession(ChannelHandlerContext ctx, SIPMessage generalSipMessage){
        return sipSessionManager.createOrGetSIPSession(ctx, generalSipMessage);
    }

    public SipSession getSipSession(SipSessionKey sipSessionKey){
        return sipSessionManager.getSipSession(sipSessionKey);
    }

    public SipApplicationSession getSipApplicationSession(SipApplicationSessionKey sipApplicationSessionKey){
        return sipSessionManager.findSipApplicationSession(sipApplicationSessionKey);
    }

    public SipApplicationSession getSipApplicationSession(SipSessionKey sipSessionKey){
        return sipSessionManager.findSipApplicationSession(sipSessionKey);
    }

    public void addClient(String host, int port, String transport, ChannelHandlerContext channelHandlerContext){
        String key=createClientKey(host, port, transport);


        this.clientMap.put(key, channelHandlerContext);
        logger.info("Add Client :: " + key);
    }

    public Optional<ChannelHandlerContext> getClientConnection(String host, int port, String transport){
        String key=createClientKey(host, port, transport);
        Optional<ChannelHandlerContext> optionalChannelHandlerContext;
        return optionalChannelHandlerContext=Optional.ofNullable(this.clientMap.get(key));
    }

    public void deleteClient(String host, int port, String transport){
        String key=createClientKey(host, port, transport);

        if(this.clientMap.containsKey(key)){
            this.clientMap.remove(key);
            logger.info("Delete Client success :: " + key);
        }
        else
            logger.info("Delete Client fail :: " + key);
    }

    private String createClientKey(String host, int port, String transport){
        return String.format("%s:%d:%s", host, port, transport);
    }

}
