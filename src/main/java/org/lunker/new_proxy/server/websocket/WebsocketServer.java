package org.lunker.new_proxy.server.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.lunker.new_proxy.server.http.HttpChannelInitializer;
import org.lunker.new_proxy.server.tcp.TCPChannelInitializer;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.stub.AbstractServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 31..
 */
public class WebsocketServer extends AbstractServer{
    private Logger logger= LoggerFactory.getLogger(WebsocketServer.class);
    private final int PORT = 5072;

    private EventLoopGroup bossGroup=null;
    private EventLoopGroup workerGroup=null;

    public WebsocketServer(ServerProcessor serverProcessor, Map<String, Object> transportConfigMap) {
        // Set Netty channel initializer
        this.channelInitializer=new TCPChannelInitializer(serverProcessor);

        // Set transport configs
        this.transportConfigMap=transportConfigMap;
    }

    @Override
    public ChannelFuture run() throws InterruptedException {
        // Configure the server.
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();

        b.option(ChannelOption.SO_BACKLOG, 1024);
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpChannelInitializer());

        ChannelFuture channelFuture=b.bind(PORT).sync(); // (7)

        logger.info("Run Websocket Server Listening on " + PORT);

        return channelFuture;
    }

    public void shutdown(){
        logger.info("Shut down Websocket Server gracefully...");
        if(bossGroup!=null)
            bossGroup.shutdownGracefully();
        if (workerGroup!=null)
            workerGroup.shutdownGracefully();
    }
}
