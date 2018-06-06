package org.lunker.new_proxy.client.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.server.tcp.TcpStreamDecoder;
import org.lunker.new_proxy.sip.processor.SipPreProcessor;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClientInitializer extends ChannelInitializer {
    private Logger logger = LoggerFactory.getLogger(TcpClientInitializer.class);

    SipMessageHandler sipMessageHandler;

    public TcpClientInitializer(SipMessageHandler sipMessageHandler) {
        this.sipMessageHandler = sipMessageHandler;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("decoder", new TcpStreamDecoder());
        ch.pipeline().addLast("preProcessor", new SipPreProcessor(Transport.TCP));
        ch.pipeline().addLast("sipMessageHandler", this.sipMessageHandler);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }
}
