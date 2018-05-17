package org.lunker.new_proxy.server.udp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.server.TransportInitializer;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hoh on 2018. 5. 15.
 */
public class UDPChannelInitializer extends TransportInitializer {
    private Logger logger = LoggerFactory.getLogger(UDPChannelInitializer.class);
    private ServerProcessor serverProcessor = null;

    public UDPChannelInitializer(ServerProcessor serverProcessor) {
        this.serverProcessor = serverProcessor;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
//        ch.pipeline().addLast("decoder", new TCPStreamDecoder());
//        ch.pipeline().addLast("decoder", new UDPServerHandler());
//        ch.pipeline().addLast("decoder", new UDPStreamDecoder());
//        ch.pipeline().addLast("preProcessor", new UDPPreProcessor(this.serverProcessor.getSipMessageHandler()));
//        ch.pipeline().addLast("postProcessor", new UDPPostProcessor());
//        ch.pipeline().addLast("preProcessor", this.serverProcessor.newPreProcessorInstance());
        ch.pipeline().addLast("handler", new UDPServerHandler(this.serverProcessor.getSipMessageHandler()));
//
//        ch.pipeline().addLast("postProcessor", this.serverProcessor.getPostProcessor());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }

}
