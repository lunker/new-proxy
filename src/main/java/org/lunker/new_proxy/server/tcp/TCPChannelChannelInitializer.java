package org.lunker.new_proxy.server.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.server.TransportChannelInitializer;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class TCPChannelChannelInitializer extends TransportChannelInitializer {
    private Logger logger= LoggerFactory.getLogger(TCPChannelChannelInitializer.class);

    public TCPChannelChannelInitializer(ServerProcessor serverProcessor) {
        this.serverProcessor=serverProcessor;

//        this.postProcessor=serverProcessor.getPostProcessor();
//        this.preProcessor=serverProcessor.getPreProcessor();

    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        // Create handlers per Client connection
        // TCP specific
        ch.pipeline().addLast("decoder", new TCPStreamDecoder());

        ch.pipeline().addLast("preProcessor", serverProcessor.newPreProcessorInstance());
//        ch.pipeline().addLast("preProcessor", preProcessor);

        // TODO: postprocessor가 transport specific 한가 ?
        // TODO: PostProcessor의 역할
//        ch.pipeline().addLast("postProcessor", this.postProcessor);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }
}
