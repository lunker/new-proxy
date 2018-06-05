package org.lunker.new_proxy.client.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import org.lunker.new_proxy.server.tcp.TcpStreamDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClientInitializer extends ChannelInitializer {
    private Logger logger = LoggerFactory.getLogger(TcpClientInitializer.class);

    private PreProcessor preProcessor;

    public TcpClientInitializer(PreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("decoder", new TcpStreamDecoder());
        ch.pipeline().addLast("preProcessor", preProcessor);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }
}
