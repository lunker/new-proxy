package org.lunker.new_proxy.client;

import io.netty.channel.ChannelFuture;
import org.lunker.new_proxy.client.tcp.TcpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class TcpConnectionGenerator implements ConnectionGenerator {
    private Logger logger = LoggerFactory.getLogger(TcpConnectionGenerator.class);
    private static List<Mono<ChannelFuture>> clientList = new ArrayList<>();

    /**
     * Lazy instance holder for singleton
     */
    private static class LazyInstanceHolder {
        private static final TcpConnectionGenerator instance = new TcpConnectionGenerator();
    }

    public static TcpConnectionGenerator getInstance() {
        return LazyInstanceHolder.instance;
    }

    @Override
    public void generate(String host, int port, Class SipMessageHandlerImpl) throws Exception {
        // TODO: use reactor (MONO)
        Mono<ChannelFuture> clientThread = Mono.fromCallable(()->{
            TcpClient tcpClient = new TcpClient(SipMessageHandlerImpl);
            logger.debug("creating tcp connection to {}:{}", host, port);
            ChannelFuture channelFuture = tcpClient.connect(host, port);
            logger.debug("connected to {}:{}", host, port);
            return channelFuture;
        });
        clientThread.subscribeOn(Schedulers.newElastic("elastic-tcp-client-"+host+":"+port));
        clientThread.subscribe();
//        ChannelFuture channelFuture = tcpClient.connect(host, port);
//        channelFuture.channel().localAddress();
//        channelFuture.channel().remoteAddress();
//        channelFuture.channel().closeFuture().await();
    }
}
