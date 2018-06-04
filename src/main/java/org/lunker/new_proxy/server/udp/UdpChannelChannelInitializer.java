package org.lunker.new_proxy.server.udp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.server.TransportChannelInitializer;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by hoh on 2018. 5. 15.
 */
public class UdpChannelChannelInitializer extends TransportChannelInitializer {
    private Logger logger = LoggerFactory.getLogger(UdpChannelChannelInitializer.class);
    private ServerProcessor serverProcessor = null;

    public UdpChannelChannelInitializer(ServerProcessor serverProcessor) {
        this.serverProcessor = serverProcessor;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
//        ch.pipeline().addLast("decoder", new TcpStreamDecoder());
//        ch.pipeline().addLast("decoder", new UdpServerHandler());
//        ch.pipeline().addLast("decoder", new UDPStreamDecoder());
//        ch.pipeline().addLast("preProcessor", new UDPPreProcessor(this.serverProcessor.getSipMessageHandler()));
//        ch.pipeline().addLast("postProcessor", new UDPPostProcessor());
//        ch.pipeline().addLast("preProcessor", this.serverProcessor.newPreProcessorInstance());

        ch.pipeline().addLast("handler", new UdpServerHandler(Optional.ofNullable(this.serverProcessor.getSipMessageHandler())));

//
//        ch.pipeline().addLast("postProcessor", this.serverProcessor.getPostProcessor());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }

}
