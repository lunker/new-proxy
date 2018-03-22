package org.lunker.new_proxy.sip.context;

import gov.nist.javax.sip.message.SIPMessage;
import org.lunker.new_proxy.sip.session.SIPSessionManagerImpl;
import org.lunker.new_proxy.stub.session.ss.SIPSession;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class ProxyContext {

    private static ProxyContext instance=null;
    private SIPSessionManagerImpl sipSessionManager=null;

    private ProxyContext() {
        sipSessionManager=new SIPSessionManagerImpl();
    }

    public static ProxyContext getInstance() {
        if (instance==null)
            instance=new ProxyContext();
        return instance;
    }

    public SIPSession createOrGetSIPSession(SIPMessage sipMessage){
        return sipSessionManager.createOrGetSIPSession(sipMessage);
    }

    public SIPSession getSIPSession(SIPMessage sipMessage){
        return sipSessionManager.getSIPSession(sipMessage);
    }

}
