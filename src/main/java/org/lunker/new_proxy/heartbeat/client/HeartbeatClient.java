package org.lunker.new_proxy.heartbeat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.lunker.new_proxy.heartbeat.HeartbeatHandler;
import org.lunker.new_proxy.heartbeat.HeartbeatInitializer;

public class HeartbeatClient {
    private EventLoopGroup eventLoopGroup;
    private HeartbeatHandler heartbeatHandler;

    private ChannelFuture channelFuture;

    public HeartbeatClient() {
        this.eventLoopGroup = new NioEventLoopGroup();
        this.heartbeatHandler = new HeartbeatClientHandler();
    }

    public HeartbeatClient(HeartbeatHandler heartbeatHandler) {
        this.eventLoopGroup = new NioEventLoopGroup();
        this.heartbeatHandler = heartbeatHandler;
    }

    public ChannelFuture connect(String host, int port) throws InterruptedException {
        Bootstrap bootstrap  = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new HeartbeatInitializer(this.heartbeatHandler))
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true);

        this.channelFuture = bootstrap.connect(host, port).sync();
        return this.channelFuture;
    }

    public void shutdown() {
        if (eventLoopGroup != null)
            eventLoopGroup.shutdownGracefully();
    }
}
