package org.lunker.new_proxy.server.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import org.lunker.new_proxy.sip.handler.SIPPostProcessor;
import org.lunker.new_proxy.sip.handler.SIPPreProcessor;
import org.lunker.new_proxy.sip.handler.SIPProcessor;
import org.lunker.new_proxy.sip.handler.SIPStreamDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class TCPChannelInitializer extends ChannelInitializer {

    private Logger logger= LoggerFactory.getLogger(TCPChannelInitializer.class);

    public TCPChannelInitializer() {
        logger.info("create!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("decoder", new SIPStreamDecoder()); // byte -> Sip String
        ch.pipeline().addLast("encoder", new SIPPreProcessor()); // Sip String -> SIPMessage (Jain) -> 우리 SipMessage
        ch.pipeline().addLast("handler", new SIPProcessor()); // invite, register 등등등 처리
        ch.pipeline().addLast("postProcessor", new SIPPostProcessor()); // 우리 SipMessage 전송 or logging
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }
}
