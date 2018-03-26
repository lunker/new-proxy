package org.lunker.new_proxy.jetty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import org.lunker.new_proxy.server.LoggingHandler;
import org.lunker.new_proxy.sip.handler.SIPPreProcessor;
import org.lunker.new_proxy.sip.handler.SIPProcessor;
import org.lunker.new_proxy.sip.handler.SIPStreamDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class ProxyChannelInitializer extends ChannelInitializer {

    private Logger logger= LoggerFactory.getLogger(ProxyChannelInitializer.class);

    public ProxyChannelInitializer() {
        logger.info("create!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
//        ch.pipeline().addLast("tcp", new TCPHandler());
//        ch.pipeline().addLast("decoder", new DelimiterBasedFrameDecoder(10000, Delimiters.lineDelimiter()));
//        ch.pipeline().addLast("decoder", new DelimiterBasedFrameDecoder(10000, Unpooled.wrappedBuffer("\r\n\r\n".getBytes())));

//        ch.pipeline().addLast("tcp", new TCPHandler());
//        ch.pipeline().addLast("parser", new SIPMessageParser());


//        ch.pipeline().addLast("decoder", new SIPMessageStreamDecoder(1024));
//        ch.pipeline().addLast("decoder", new SIPByteDecoder());
        ch.pipeline().addLast("decoder", new SIPStreamDecoder());
        ch.pipeline().addLast("encoder", new SIPPreProcessor());
        ch.pipeline().addLast("handler", new SIPProcessor());
        ch.pipeline().addLast("logging", new LoggingHandler());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }
}
