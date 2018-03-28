package org.lunker.new_proxy.sip.context;

import gov.nist.javax.sip.message.SIPMessage;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.sip.session.SIPSessionManagerImpl;
import org.lunker.new_proxy.sip.session.sas.SipApplicationSessionKey;
import org.lunker.new_proxy.sip.session.ss.SipSessionKey;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;
import org.lunker.new_proxy.stub.session.sas.SipApplicationSession;
import org.lunker.new_proxy.stub.session.ss.SipSession;
import org.lunker.new_proxy.util.Registrar;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class ProxyContext {

    private static ProxyContext instance=null;
    private SIPSessionManagerImpl sipSessionManager=null;
    private Registrar registrar=null;


    private ProxyContext() {
        this.sipSessionManager=new SIPSessionManagerImpl();
        this.registrar=Registrar.getInstance();
    }

    public static ProxyContext getInstance() {
        if (instance==null)
            instance=new ProxyContext();
        return instance;
    }

    public SipSession createOrGetSIPSession(ChannelHandlerContext ctx, GeneralSipMessage generalSipMessage){
        return sipSessionManager.createOrGetSIPSession(ctx, generalSipMessage.getRawSipMessage());
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

    public Registrar getRegistrar() {
        return registrar;
    }


}
