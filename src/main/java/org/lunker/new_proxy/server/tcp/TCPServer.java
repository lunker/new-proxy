package org.lunker.new_proxy.server.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.stub.AbstractServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 15..
 */
public class TCPServer extends AbstractServer {
    private Logger logger= LoggerFactory.getLogger(TCPServer.class);

    //TODO: refactoring
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public TCPServer(ServerProcessor serverProcessor, Map<String, Object> transportConfigMap) {

        // Set Channel Initializer
        this.channelInitializer=new TCPChannelInitializer(serverProcessor);
        // Set Transport Configs
        this.transportConfigMap=transportConfigMap;
    }

    /**
     * Run TCPServer
     * @return
     * @throws Exception
     */
    @Override
    public ChannelFuture run() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(this.channelInitializer)
                .option(ChannelOption.SO_BACKLOG, 20000)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_RCVBUF, 20000);

        // Bind and start to accept incoming connections.
        ChannelFuture f = b.bind((int) transportConfigMap.get("port")).sync(); // (7)

        logger.info("Run TCP Server Listening on " + (int) transportConfigMap.get("port"));

        // Wait until the server socket is closed.
        // In this example, this does not happen, but you can do that to gracefully
        // shut down your server.
        f.channel().closeFuture().sync();
        return f;
    }// end run

    public void shutdown(){
        logger.debug("Shut down TCPServer gracefully...");

        if(workerGroup!=null)
            workerGroup.shutdownGracefully();
        if(bossGroup!=null)
            bossGroup.shutdownGracefully();
    }
}
