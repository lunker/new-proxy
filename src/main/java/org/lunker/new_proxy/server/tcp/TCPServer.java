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
        // Set Netty channel initializer
        this.channelInitializer=new TCPChannelInitializer(serverProcessor);

        // Set transport configs
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

        // TODO: set ChannelOption using transport properties
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(this.channelInitializer)
                .option(ChannelOption.SO_BACKLOG, 2048)

                .childOption(ChannelOption.SO_LINGER, 0)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)

                .childOption(ChannelOption.SO_RCVBUF, 200 * 1024)
                .childOption(ChannelOption.SO_SNDBUF, 200 * 1024); // 5- > 10 cause 메세지 유실은 사라짐


        // Bind and start to accept incoming connections.
        ChannelFuture f = b.bind((int) transportConfigMap.get("port")).sync(); // (7)

        logger.info("Run TCP Server Listening on {}", transportConfigMap.get("port"));

        f.channel().closeFuture().sync();
        return f;
    }// end run

    public void shutdown(){
        if(logger.isDebugEnabled())
            logger.debug("Shut down TCPServer gracefully...");

        if(workerGroup!=null)
            workerGroup.shutdownGracefully();
        if(bossGroup!=null)
            bossGroup.shutdownGracefully();
    }
}
