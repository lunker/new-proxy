package org.lunker.new_proxy.sip.session;

import gov.nist.javax.sip.message.SIPMessage;
import org.lunker.new_proxy.exception.InvalidArgumentException;
import org.lunker.new_proxy.sip.session.sas.SIPApplicationSessionImpl;
import org.lunker.new_proxy.sip.session.sas.SIPApplicationSessionKey;
import org.lunker.new_proxy.sip.session.ss.SIPSessionImpl;
import org.lunker.new_proxy.sip.session.ss.SIPSessionKey;
import org.lunker.new_proxy.stub.session.SIPSessionManager;
import org.lunker.new_proxy.stub.session.sas.SIPApplicationSession;
import org.lunker.new_proxy.stub.session.ss.SIPSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dongqlee on 2018. 3. 20..
 */

/**
 * CRUD for SIPSession & SIPApplicatoinSession
 */
public class SIPSessionManagerImpl implements SIPSessionManager{

    private Logger logger= LoggerFactory.getLogger(SIPSessionManagerImpl.class);
    private int INITIAL_CAPACITY=1024;
    private ConcurrentHashMap<SIPSessionKey, SIPSession> sipSessionConcurrentHashMap;
    private ConcurrentHashMap<SIPApplicationSessionKey, SIPApplicationSession> sipApplicationSessionConcurrentHashMap;

    public SIPSessionManagerImpl() {
        this.sipApplicationSessionConcurrentHashMap=new ConcurrentHashMap<>(INITIAL_CAPACITY * 2);
        this.sipSessionConcurrentHashMap=new ConcurrentHashMap<>(INITIAL_CAPACITY);
    }

    public SIPSession createOrGetSIPSession(SIPMessage sipMessage) {
        // create SIPSession
        // using SIPmessage

        SIPSessionKey currentSIPSessionKey=null;
        SIPSession currentSIPSession=null;
        String fromTag=sipMessage.getFromTag();
        String toTag=sipMessage.getToTag();

        SIPApplicationSession currentCallSipApplicationSession=null;
        currentCallSipApplicationSession=findSipApplicationSession(fromTag, toTag);
        logger.info(String.format("FromTag: %s, ToTag: %s", fromTag, toTag));

        if(currentCallSipApplicationSession==null){
            // first comming request

            // create SAS && SS
            currentCallSipApplicationSession=createSIPApplicationSession();

            currentSIPSessionKey=new SIPSessionKey(sipMessage.getFromTag(), sipMessage.getCallId().getCallId(), currentCallSipApplicationSession.getSipApplicationKey().getGeneratedKey());
            currentSIPSession=new SIPSessionImpl(currentSIPSessionKey);
            currentCallSipApplicationSession.addSIPSession(currentSIPSession);

            this.sipSessionConcurrentHashMap.put(currentSIPSessionKey, currentSIPSession);

            logger.info(String.format("Create SAS : %s", currentCallSipApplicationSession.getSipApplicationKey().getGeneratedKey()));
        }
        else{
            currentSIPSessionKey=new SIPSessionKey(sipMessage.getFromTag(), sipMessage.getCallId().getCallId(), "");
            currentSIPSession=sipSessionConcurrentHashMap.get(currentSIPSessionKey);
        }

        return currentSIPSession;
    }

    public SIPApplicationSession findSipApplicationSession(String fromTag, String toTag){
        SIPApplicationSession sipApplicationSession=null;

        sipApplicationSession=findSipApplicationSession(fromTag);

        if(sipApplicationSession==null)
            sipApplicationSession=findSipApplicationSession(toTag);

        return sipApplicationSession;
    }

    private SIPApplicationSession findSipApplicationSession(String tag){
        String sasId="";

        if(tag ==null || tag.length() < 8){
            return null;
        }

        sasId=tag.substring(tag.length()-7, tag.length());

        return sipApplicationSessionConcurrentHashMap.get(sasId);
    }

    public SIPApplicationSession createSIPApplicationSession() {
        SIPApplicationSessionKey sipApplicationSessionKey=new SIPApplicationSessionKey();
        SIPApplicationSession sipApplicationSession=null;

        try{
            sipApplicationSession=new SIPApplicationSessionImpl(sipApplicationSessionKey);
            this.sipApplicationSessionConcurrentHashMap.put(sipApplicationSessionKey, sipApplicationSession);
        }
        catch (InvalidArgumentException iae){
            iae.printStackTrace();
        }

        return sipApplicationSession;
    }

    public SIPSession getSIPSession(SIPMessage sipMessage){
        SIPApplicationSession sipApplicationSession=null;
        sipApplicationSession=findSipApplicationSession(sipMessage.getFromTag(), sipMessage.getToTag());

        SIPSessionKey currentSIPSessionKey=new SIPSessionKey(sipMessage.getFromTag(), sipMessage.getCallId().getCallId(), sipApplicationSession.getSipApplicationKey().getGeneratedKey());

        return sipSessionConcurrentHashMap.get(currentSIPSessionKey);
    }
}
