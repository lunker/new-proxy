package org.lunker.new_proxy.server.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.lunker.new_proxy.server.http.HttpChannelInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 31..
 */
public class WebsocketServer {

    private Logger logger= LoggerFactory.getLogger(WebsocketServer.class);
    private final int PORT = 5072;

    private EventLoopGroup bossGroup=null;
    private EventLoopGroup workerGroup=null;

    public void run() throws InterruptedException {
        // Configure the server.
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        /*

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
//                    .processor(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpChannelInitializer());

            Channel ch = b.bind(PORT).sync().channel();

            logger.info("Run Websocket Server Listening on " + PORT);

            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
        */

        ServerBootstrap b = new ServerBootstrap();
        b.option(ChannelOption.SO_BACKLOG, 1024);
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
//                    .processor(new LoggingHandler(LogLevel.INFO))
                .childHandler(new HttpChannelInitializer());

        ChannelFuture f = b.bind(PORT).sync(); // (7)

        logger.info("Run Websocket Server Listening on " + PORT);

//        f.channel().closeFuture().sync();
    }

    public void shutdown(){
        logger.info("Shut down Websocket Server gracefully...");
        if(bossGroup!=null)
            bossGroup.shutdownGracefully();
        if (workerGroup!=null)
            workerGroup.shutdownGracefully();
    }
}
