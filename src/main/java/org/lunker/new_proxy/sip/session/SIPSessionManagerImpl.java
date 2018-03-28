package org.lunker.new_proxy.sip.session;

import gov.nist.javax.sip.message.SIPMessage;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.exception.InvalidArgumentException;
import org.lunker.new_proxy.sip.session.sas.SipApplicationSessionImpl;
import org.lunker.new_proxy.sip.session.sas.SipApplicationSessionKey;
import org.lunker.new_proxy.sip.session.ss.SipSessionImpl;
import org.lunker.new_proxy.sip.session.ss.SipSessionKey;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;
import org.lunker.new_proxy.stub.session.SIPSessionManager;
import org.lunker.new_proxy.stub.session.sas.SipApplicationSession;
import org.lunker.new_proxy.stub.session.ss.SipSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dongqlee on 2018. 3. 20..
 */

/**
 * CRUD for SipSession & SIPApplicatoinSession
 */
public class SIPSessionManagerImpl implements SIPSessionManager{

    private Logger logger= LoggerFactory.getLogger(SIPSessionManagerImpl.class);
    private int INITIAL_CAPACITY=1024;
    private ConcurrentHashMap<SipSessionKey, SipSession> sipSessionConcurrentHashMap;
    private ConcurrentHashMap<SipApplicationSessionKey, SipApplicationSession> sipApplicationSessionConcurrentHashMap;

    public SIPSessionManagerImpl() {
        this.sipApplicationSessionConcurrentHashMap=new ConcurrentHashMap<>(INITIAL_CAPACITY * 2);
        this.sipSessionConcurrentHashMap=new ConcurrentHashMap<>(INITIAL_CAPACITY);
    }

    public SipSession createOrGetSIPSession(ChannelHandlerContext ctx, GeneralSipMessage generalSipMessage) {
        return createOrGetSIPSession(ctx, generalSipMessage.getRawSipMessage());
    }

    public SipSession createOrGetSIPSession(ChannelHandlerContext ctx, SIPMessage generalSipMessage) {
        // create SipSession
        // using SIPmessage

        SipSessionKey currentSipSessionKey =null;
        SipSession currentSipSession =null;

        String fromTag=generalSipMessage.getFrom().getTag();
        String toTag=generalSipMessage.getTo().getTag();

        SipApplicationSession currentCallSipApplicationSession=null;
        currentCallSipApplicationSession=findSipApplicationSession(fromTag, toTag);
        logger.info(String.format("FromTag: %s, ToTag: %s", fromTag, toTag));

        if(currentCallSipApplicationSession==null){
            // first comming request

            // create SAS && SS
            currentCallSipApplicationSession=createSipApplicationSession();

            currentSipSessionKey =new SipSessionKey(generalSipMessage, currentCallSipApplicationSession.getSipApplicationKey().getGeneratedKey());
            currentSipSession =new SipSessionImpl(currentSipSessionKey, currentCallSipApplicationSession.getSipApplicationKey());

            // Add SipSession as child of SAS
            currentCallSipApplicationSession.addSipSession(currentSipSession);

            this.sipSessionConcurrentHashMap.put(currentSipSessionKey, currentSipSession);

            logger.info(String.format("Create SAS : %s", currentCallSipApplicationSession.getSipApplicationKey().getGeneratedKey()));
        }
        else{
            currentSipSessionKey =new SipSessionKey(generalSipMessage, currentCallSipApplicationSession.getSipApplicationKey().getGeneratedKey());
            currentSipSession=sipSessionConcurrentHashMap.get(currentSipSessionKey);
        }

        currentSipSession.setCtx(ctx);

        return currentSipSession;
    }

    public SipApplicationSession findSipApplicationSession(String fromTag, String toTag){
        SipApplicationSession sipApplicationSession=null;

        sipApplicationSession=findSipApplicationSession(fromTag);

        if(sipApplicationSession==null)
            sipApplicationSession=findSipApplicationSession(toTag);

        return sipApplicationSession;
    }

    public SipApplicationSession findSipApplicationSession(SipApplicationSessionKey sipApplicationSessionKey){
        return this.sipApplicationSessionConcurrentHashMap.get(sipApplicationSessionKey);
    }

    public SipApplicationSession findSipApplicationSession(SipSessionKey sipSessionKey){
        SipApplicationSessionKey sipApplicationSessionKey=new SipApplicationSessionKey(sipSessionKey.getApplicationSessionId());

        return this.sipApplicationSessionConcurrentHashMap.get(sipApplicationSessionKey);
    }


    private SipApplicationSession findSipApplicationSession(String tag){
        String sasId="";

        if(tag ==null || tag.length() < 8){
            return null;
        }

        sasId=tag.substring(tag.length()-7, tag.length());

        return sipApplicationSessionConcurrentHashMap.get(sasId);
    }

    public SipApplicationSession createSipApplicationSession() {
        SipApplicationSessionKey sipApplicationSessionKey=new SipApplicationSessionKey();
        SipApplicationSession sipApplicationSession=null;

        try{
            sipApplicationSession=new SipApplicationSessionImpl(sipApplicationSessionKey);
            this.sipApplicationSessionConcurrentHashMap.put(sipApplicationSessionKey, sipApplicationSession);
        }
        catch (InvalidArgumentException iae){
            iae.printStackTrace();
        }

        return sipApplicationSession;
    }

    public SipSession getSipSession(SipSessionKey sipSessionKey){
        return sipSessionConcurrentHashMap.get(sipSessionKey);
    }

    public SipSession getSipSession(GeneralSipMessage generalSipMessage){
        return this.getSipSession(generalSipMessage.getSipSessionKey());
    }
}
