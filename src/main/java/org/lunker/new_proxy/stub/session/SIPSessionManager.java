package org.lunker.new_proxy.stub.session;

import org.lunker.new_proxy.stub.session.ss.SIPSession;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public interface SIPSessionManager {

    SIPSession createAndJoinSIPSession();
}
