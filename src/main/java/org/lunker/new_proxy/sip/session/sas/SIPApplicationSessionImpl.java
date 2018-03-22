package org.lunker.new_proxy.sip.session.sas;

import org.lunker.new_proxy.exception.InvalidArgumentException;
import org.lunker.new_proxy.sip.session.ss.SIPSessionKey;
import org.lunker.new_proxy.stub.session.sas.SIPApplicationSession;
import org.lunker.new_proxy.stub.session.ss.SIPSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class SIPApplicationSessionImpl implements SIPApplicationSession {

    private Logger logger= LoggerFactory.getLogger(SIPApplicationSessionImpl.class);
    private SIPApplicationSessionKey sipApplicationSessionKey=null;
    private LocalDateTime createdTime=null;

    private HashMap<SIPSessionKey, SIPSession> sipSessions;

    private SIPApplicationSessionImpl() {

    }

    public SIPApplicationSessionImpl(SIPApplicationSessionKey sipApplicationSessionKey) throws InvalidArgumentException{
        if(sipApplicationSessionKey==null){
            throw new InvalidArgumentException("Not valid sipApplicationSessionKey is entered");
        }
        this.sipApplicationSessionKey=sipApplicationSessionKey;
        this.sipSessions=new HashMap<>();
    }

    public void addSession(SIPSession sipSession){
        this.sipSessions.put(sipSession.getSipSessionkey(), sipSession);
    }

    @Override
    public SIPApplicationSessionKey getSipApplicationKey() {
        return this.sipApplicationSessionKey;
    }

    @Override
    public void addSIPSession(SIPSession sipSession) {
        this.sipSessions.put(sipSession.getSipSessionkey(), sipSession);
    }
}
