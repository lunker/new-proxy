package org.lunker.new_proxy.server.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpServerCodec;
import org.lunker.new_proxy.server.TransportInitializer;
import org.lunker.new_proxy.server.http.HttpServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 5. 29..
 */
public class WebsocketChannelInitializer extends TransportInitializer{
    private Logger logger= LoggerFactory.getLogger(WebsocketChannelInitializer.class);

    @Override
    protected void initChannel(Channel ch) throws Exception {
//        ChannelPipeline pipeline = socketChannel.pipeline();
        ch.pipeline().addLast("httpServerCodec", new HttpServerCodec());
        ch.pipeline().addLast("httpHandler", new HttpServerHandler());
    }
}
