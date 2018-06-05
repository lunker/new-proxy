package org.lunker.new_proxy.server.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.server.TransportChannelInitializer;
import org.lunker.new_proxy.stub.AbstractServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 15..
 */
public class TcpServer extends AbstractServer {
    private Logger logger= LoggerFactory.getLogger(TcpServer.class);

    //TODO: refactoring
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Transport transport=Transport.TCP;
    private Configuration configuration=Configuration.getInstance();

    public TcpServer(TransportChannelInitializer transportChannelInitializer) {
        // Set Netty channel initializer
//        this.channelInitializer=new TcpChannelChannelInitializer(serverProcessor);
        this.channelInitializer=transportChannelInitializer;
    }


    /**
     * Run TcpServer
     * @return
     * @throws Exception
     */
    @Override
    public ChannelFuture run() throws Exception {

        //
        configuration.getConfigMap(transport);
        ServerBootstrap b = new ServerBootstrap();

        Map<String, Object> tcpOptions=(Map<String, Object>)transportConfigMap.get("options");
        // TODO: set ChannelOption using transport properties
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(this.channelInitializer)
                .option(ChannelOption.SO_BACKLOG, (int) tcpOptions.get("so_backlog"))

                .childOption(ChannelOption.SO_LINGER, (int) tcpOptions.get("so_linger"))
                .childOption(ChannelOption.TCP_NODELAY, (boolean) tcpOptions.get("tcp_nodelay"))
                .childOption(ChannelOption.SO_REUSEADDR, (boolean) tcpOptions.get("so_reuseaddr"))

                .childOption(ChannelOption.SO_RCVBUF, (int) tcpOptions.get("so_rcvbuf"))
                .childOption(ChannelOption.SO_SNDBUF, (int) tcpOptions.get("so_sndbuf"));

        // Bind and addHandler to accept incoming connections.
        ChannelFuture channelFuture=b.bind((int) transportConfigMap.get("port")); // (7)

        if(logger.isInfoEnabled())
            logger.info("Run TCP Server Listening on {}", transportConfigMap.get("port"));

        return channelFuture;
    }// end run

    public void shutdown(){
        if(logger.isDebugEnabled())
            logger.debug("Shut down TcpServer gracefully...");

        if(workerGroup!=null)
            workerGroup.shutdownGracefully();
        if(bossGroup!=null)
            bossGroup.shutdownGracefully();
    }
}

