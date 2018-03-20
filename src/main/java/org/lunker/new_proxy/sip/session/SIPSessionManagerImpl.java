package org.lunker.new_proxy.sip.session;

import org.lunker.new_proxy.stub.session.SIPSessionManager;
import org.lunker.new_proxy.stub.session.sas.SIPApplicationSession;
import org.lunker.new_proxy.stub.session.sas.SIPApplicationSessionKey;
import org.lunker.new_proxy.stub.session.ss.SIPSession;
import org.lunker.new_proxy.stub.session.ss.SIPSessionKey;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dongqlee on 2018. 3. 20..
 */

/**
 * CRUD for SIPSession & SIPApplicatoinSession
 */
public class SIPSessionManagerImpl implements SIPSessionManager{

    private int INITIAL_CAPACITY=1024;
    private ConcurrentHashMap<SIPSessionKey, SIPSession> sipSessionConcurrentHashMap;
    private ConcurrentHashMap<SIPApplicationSessionKey, SIPApplicationSession> sipApplicationSessionConcurrentHashMap;

    public SIPSessionManagerImpl() {
        this.sipApplicationSessionConcurrentHashMap=new ConcurrentHashMap<>(INITIAL_CAPACITY * 2);
        this.sipSessionConcurrentHashMap=new ConcurrentHashMap<>(INITIAL_CAPACITY);
    }


    @Override
    public SIPSession createAndJoinSIPSession() {
        return null;
    }

    public SIPSession findSIPSession(){
        return null;
    }

}
