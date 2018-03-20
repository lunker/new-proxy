package org.lunker.new_proxy.server.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.lunker.new_proxy.jetty.ProxyChannelInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 15..
 */
public class TCPServer extends ChannelInboundHandlerAdapter {

    private Logger logger= LoggerFactory.getLogger(TCPServer.class);
    private int port=10010;

    public TCPServer() {
    }

    public TCPServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ProxyChannelInitializer())
                    .option(ChannelOption.SO_BACKLOG, 2048)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // (6)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_RCVBUF, 2000)
                    .childOption(ChannelOption.SO_REUSEADDR, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            logger.info("Listening on " + port);

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            logger.info("Shut down proxy gracefully...");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }// end run
}
