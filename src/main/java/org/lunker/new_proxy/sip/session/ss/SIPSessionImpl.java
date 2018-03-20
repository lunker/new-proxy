package org.lunker.new_proxy.sip.session.ss;

import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.stub.session.ss.SIPSessionKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class SIPSessionImpl  {

    private SIPSessionKey sipSessionKey;
    private ChannelHandlerContext ctx;
    private Map<String, Object> sessionAttributes;

    public SIPSessionImpl() {

    }

    public SIPSessionImpl(SIPSessionKey sipSessionKey) {
        this.sipSessionKey = sipSessionKey;
        this.sessionAttributes=new HashMap<>();
    }

//    public SIPSessionKey getSipSessionKey() {
//        return sipSessionKey;
//    }

    public String getSipSessionKey(){
        return sipSessionKey.getKey();
    }
}

