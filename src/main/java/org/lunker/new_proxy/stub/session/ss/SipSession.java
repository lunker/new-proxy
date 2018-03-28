package org.lunker.new_proxy.stub.session.ss;

import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.sip.session.ss.SipSessionKey;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipRequest;
import org.lunker.new_proxy.stub.session.sas.SipApplicationSession;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public interface SipSession {
    SipApplicationSession getSipApplicationSession();

    SipSessionKey getSipSessionkey();

    void setAttribute(String key, Object value);

    GeneralSipRequest createRequest(String method);

    void setFirstRequest(GeneralSipRequest generalSipRequest);

    void setCtx(ChannelHandlerContext ctx);

    ChannelHandlerContext getCtx();
}
