package org.lunker.new_proxy.server.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.server.TransportInitializer;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.sip.processor.TCPStreamDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class TCPChannelInitializer extends TransportInitializer {
    private Logger logger= LoggerFactory.getLogger(TCPChannelInitializer.class);


    public TCPChannelInitializer(ServerProcessor serverProcessor) {
        this.postProcessor=serverProcessor.getPostProcessor();
        this.preProcessor=serverProcessor.getPreProcessor();
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        // TCP specific
        ch.pipeline().addLast("decoder", new TCPStreamDecoder());

        ch.pipeline().addLast("preProcessor", this.preProcessor);

        // TODO: postprocessor가 transport specific 한가 ?
        ch.pipeline().addLast("postProcessor", this.postProcessor);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }
}
