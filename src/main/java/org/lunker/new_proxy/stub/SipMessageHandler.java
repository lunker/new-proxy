package org.lunker.new_proxy.stub;

import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 25..
 */
public interface SipMessageHandler {
    void handle(ChannelHandlerContext ctx, Optional<DefaultSipMessage> generalSipMessage);
}
