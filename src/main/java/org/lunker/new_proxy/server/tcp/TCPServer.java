package org.lunker.new_proxy.server.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.lunker.new_proxy.stub.SipServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 15..
 */
public class TCPServer extends ChannelInboundHandlerAdapter {

    private Logger logger= LoggerFactory.getLogger(TCPServer.class);

    private int port=10010; //TODO: Get Server port from Property
    private EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private TCPChannelInitializer tcpChannelInitializer=null;

    public TCPServer() {
        tcpChannelInitializer=new TCPChannelInitializer();
    }

    public TCPServer(int port) {
        this.port = port;
    }

    /**
     * Run TCPServer
     * @return
     * @throws Exception
     */
    public ChannelFuture run() throws Exception {
        ServerBootstrap b = new ServerBootstrap(); // (2)
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) // (3)
                .childHandler(tcpChannelInitializer)
                .option(ChannelOption.SO_BACKLOG, 20000)          // (5)
                .childOption(ChannelOption.SO_KEEPALIVE, true) // (6)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_RCVBUF, 20000);

        // Bind and start to accept incoming connections.
        ChannelFuture f = b.bind(port).sync(); // (7)

        logger.info("Run TCP Server Listening on " + port);

        // Wait until the server socket is closed.
        // In this example, this does not happen, but you can do that to gracefully
        // shut down your server.
        f.channel().closeFuture().sync();
        return f;
    }// end run

    /**
     * Add SipServlet Handlers
     * @param handler
     */
    public void addHandler(SipServlet handler){
        if(tcpChannelInitializer == null) {
            // TODO: throw valid exception
        }

        tcpChannelInitializer.addHandler(handler);
    }

    public void shutdown(){
        logger.debug("Shut down TCPServer gracefully...");

        if(workerGroup!=null)
            workerGroup.shutdownGracefully();
        if(bossGroup!=null)
            bossGroup.shutdownGracefully();
    }
}
