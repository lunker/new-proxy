package org.lunker.new_proxy.heartbeat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.lunker.new_proxy.heartbeat.HeartbeatHandler;
import org.lunker.new_proxy.heartbeat.HeartbeatInitializer;

public class HeartbeatServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private HeartbeatHandler heartbeatHandler;

    int listenPort;

    public HeartbeatServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        this.heartbeatHandler = new HeartbeatServerHandler();
        this.listenPort = 2000;
    }

    public HeartbeatServer(int listenPort) {
        this();
        this.listenPort = listenPort;
    }

    public HeartbeatServer(HeartbeatHandler heartbeatHandler) {
        this();
        this.heartbeatHandler = heartbeatHandler;
    }

    public HeartbeatServer(HeartbeatHandler heartbeatHandler, int listenPort) {
        this();
        this.heartbeatHandler = heartbeatHandler;
        this.listenPort = listenPort;
    }

    public ChannelFuture run() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new HeartbeatInitializer(this.heartbeatHandler))
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true);
        // TODO: set port from configuration
        ChannelFuture channelFuture = serverBootstrap.bind(2000);

        return channelFuture;
    }

    public void shutdown() {
        if (workerGroup != null)
            workerGroup.shutdownGracefully();
        if (bossGroup != null)
            bossGroup.shutdownGracefully();
    }
}
