package org.lunker.new_proxy.sip.session.ss;

import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.sip.session.sas.SIPApplicationSessionKey;
import org.lunker.new_proxy.stub.session.sas.SIPApplicationSession;
import org.lunker.new_proxy.stub.session.ss.SIPSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class SIPSessionImpl implements SIPSession{

    private SIPSessionKey sipSessionKey;
    private SIPApplicationSessionKey sipApplicationSessionKey;
    private SIPApplicationSession sipApplicationSession;

    private ChannelHandlerContext ctx;
    private Map<String, Object> sessionAttributes;

    private SIPSessionImpl() {

    }

    public SIPSessionImpl(SIPSessionKey sipSessionKey) {
        this.sipSessionKey = sipSessionKey;
        this.sessionAttributes=new HashMap<>();
    }


    @Override
    public SIPApplicationSession getSIPApplicationSession() {
        return null;
    }

    @Override
    public SIPSessionKey getSipSessionkey() {
        return this.sipSessionKey;
    }

    @Override
    public void addAttribute(String key, Object value) {
        this.sessionAttributes.put(key, value);
    }
}

