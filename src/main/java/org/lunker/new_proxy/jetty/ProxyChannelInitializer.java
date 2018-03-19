package org.lunker.new_proxy.jetty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import org.lunker.new_proxy.server.LoggingHandler;
import org.lunker.new_proxy.server.SIPHandler;
import org.lunker.new_proxy.server.SIPMessageEncoder;
import org.lunker.new_proxy.server.SIPMessageStreamDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class ProxyChannelInitializer extends ChannelInitializer {

    private Logger logger= LoggerFactory.getLogger(ProxyChannelInitializer.class);

    @Override
    protected void initChannel(Channel ch) throws Exception {
//        ch.pipeline().addLast("tcp", new TCPHandler());
//        ch.pipeline().addLast("decoder", new DelimiterBasedFrameDecoder(10000, Delimiters.lineDelimiter()));
//        ch.pipeline().addLast("decoder", new DelimiterBasedFrameDecoder(10000, Unpooled.wrappedBuffer("\r\n\r\n".getBytes())));

//        ch.pipeline().addLast("tcp", new TCPHandler());
//        ch.pipeline().addLast("parser", new SIPMessageParser());


        ch.pipeline().addLast("decoder", new SIPMessageStreamDecoder(10000));
        ch.pipeline().addLast("encoder", new SIPMessageEncoder());
        ch.pipeline().addLast("handler", new SIPHandler());
        ch.pipeline().addLast("logging", new LoggingHandler());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
//        super.exceptionCaught(ctx, cause);
    }
}
