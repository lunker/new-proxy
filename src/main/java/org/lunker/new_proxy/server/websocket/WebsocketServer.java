package org.lunker.new_proxy.server.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.lunker.new_proxy.server.tcp.TCPChannelInitializer;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.stub.AbstractServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 31..
 */
public class WebsocketServer extends AbstractServer{
    private Logger logger= LoggerFactory.getLogger(WebsocketServer.class);
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
        SslContext sslCtx=null;

        if (true) {
            try{
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(new File("/Users/voiceloco/work/sslkey/_wildcard_voiceloco_com.crt"), new File("/Users/voiceloco/work/sslkey/voiceloco.com.key")).build();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        } else {
            sslCtx = null;
        }

        // Configure the server.
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();

        b.option(ChannelOption.SO_BACKLOG, 1024);
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
//                .childHandler(new HttpChannelInitializer(sslCtx));
                .childHandler(new WebsocketChannelInitializer(sslCtx));

        ChannelFuture channelFuture=b.bind((int) transportConfigMap.get("port")).sync(); // (7)

        logger.info("Run Websocket Server Listening on " + (int) transportConfigMap.get("port"));

        return channelFuture;
    }

    // TODO:
    public void shutdown(){
        logger.info("Shut down Websocket Server gracefully...");
        if(bossGroup!=null)
            bossGroup.shutdownGracefully();
        if (workerGroup!=null)
            workerGroup.shutdownGracefully();
    }
}
