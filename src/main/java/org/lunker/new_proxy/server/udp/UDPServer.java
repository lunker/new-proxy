package org.lunker.new_proxy.server.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.stub.AbstractServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UDPServer extends AbstractServer {
    private Logger logger = LoggerFactory.getLogger(UDPServer.class);

    private final EventLoopGroup udpGroup = new NioEventLoopGroup();

    private Map<String, Object> configMap;

    public UDPServer(ServerProcessor serverProcessor, Map<String, Object> configMap) {
        // Set Netty channel initializer
        this.channelInitializer = new UDPChannelInitializer(serverProcessor);

        // Set transport configs
        this.configMap = configMap;
    }

    @Override
    public ChannelFuture run() throws Exception {
        try {
            // TODO: change to own Bootstrap
            final Bootstrap b = new Bootstrap();
            // TODO: add something needeed server options
//            b.group(udpGroup)
//                    .channel(NioDatagramChannel.class)
//                    .handler(this.channelInitializer);
            b.group(udpGroup)
                    .channel(NioDatagramChannel.class)
                    .handler(this.channelInitializer);

            ChannelFuture f = b.bind((int) configMap.get("port")).sync();

            logger.info("Run UDP Server Listening on {}", configMap.get("port"));

            f.channel().closeFuture().sync();
            return f;
        } finally {
            if (logger.isDebugEnabled())
                logger.debug("Shut down UDPServer gracefully...");

            if (udpGroup != null) {
                udpGroup.shutdownGracefully();
            }
        }
    }
}
