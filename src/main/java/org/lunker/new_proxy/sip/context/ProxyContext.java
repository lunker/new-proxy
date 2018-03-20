package org.lunker.new_proxy.sip.context;

import org.lunker.new_proxy.sip.session.SIPSessionManagerImpl;
import org.lunker.new_proxy.stub.session.SIPSessionManager;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class ProxyContext {

    private static ProxyContext instance=null;
    private SIPSessionManager sipSessionManager=null;

    private ProxyContext() {
        sipSessionManager=new SIPSessionManagerImpl();
    }

    public static ProxyContext getInstance() {
        if (instance==null)
            instance=new ProxyContext();
        return instance;
    }
}
