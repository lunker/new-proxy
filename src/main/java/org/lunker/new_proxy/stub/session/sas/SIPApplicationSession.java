package org.lunker.new_proxy.stub.session.sas;


import org.lunker.new_proxy.sip.session.sas.SIPApplicationSessionKey;
import org.lunker.new_proxy.stub.session.ss.SIPSession;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public interface SIPApplicationSession {

    SIPApplicationSessionKey getSipApplicationKey();

    void addSIPSession(SIPSession sipSession);
}
