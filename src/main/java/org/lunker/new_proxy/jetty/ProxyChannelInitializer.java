package org.lunker.new_proxy.jetty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.lunker.new_proxy.server.LoggingHandler;
import org.lunker.new_proxy.server.SIPHandler;
import org.lunker.new_proxy.server.SIPMessageParser;
import org.lunker.new_proxy.server.TCPHandler;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class ProxyChannelInitializer extends ChannelInitializer {



    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("tcp", new TCPHandler());
        ch.pipeline().addLast("parser", new SIPMessageParser());
        ch.pipeline().addLast("handler", new SIPHandler());
        ch.pipeline().addLast("logging", new LoggingHandler());
    }
}
