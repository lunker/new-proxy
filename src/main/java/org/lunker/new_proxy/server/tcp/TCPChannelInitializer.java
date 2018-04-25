package org.lunker.new_proxy.server.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import org.lunker.new_proxy.sip.handler.SIPPostProcessor;
import org.lunker.new_proxy.sip.handler.SIPPreProcessor;
import org.lunker.new_proxy.sip.handler.SIPProcessor;
import org.lunker.new_proxy.sip.handler.SIPStreamDecoder;
import org.lunker.new_proxy.stub.SipServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class TCPChannelInitializer extends ChannelInitializer {

    private Logger logger= LoggerFactory.getLogger(TCPChannelInitializer.class);
    private List<? extends SipServlet> sipServlets=null;

    public TCPChannelInitializer() {
        sipServlets=new ArrayList<>();
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("decoder", new SIPStreamDecoder());
        ch.pipeline().addLast("encoder", new SIPPreProcessor());


        ch.pipeline().addLast("handler", new SIPProcessor());
        ch.pipeline().addLast("postProcessor", new SIPPostProcessor());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }
}
