package org.lunker.new_proxy.heartbeat;

import io.netty.channel.Channel;
import io.netty.handler.timeout.IdleStateHandler;
import org.lunker.new_proxy.server.TransportChannelInitializer;
import org.lunker.new_proxy.server.tcp.TcpStreamDecoder;

public class HeartbeatInitializer extends TransportChannelInitializer {
    private HeartbeatHandler heartbeatHandler;

    private int readerIdleTimeSeconds; // timeout if there is no readable data (ping) from last ping. server set this timer.
    private int writerIdleTimeSeconds; // timeout if time passed from last ping. client set this timer.
    private int allIdleTimeSeconds;

    public HeartbeatInitializer(HeartbeatHandler heartbeatHandler) {
        this.heartbeatHandler = heartbeatHandler;
        this.readerIdleTimeSeconds = 100;
        this.writerIdleTimeSeconds = 10;
        this.allIdleTimeSeconds = 0;
    }

    // Use this Constructor for customize heartbeat interval.
    public HeartbeatInitializer(HeartbeatHandler heartbeatHandler, int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        this.heartbeatHandler = heartbeatHandler;
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
        this.allIdleTimeSeconds = allIdleTimeSeconds;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        // byte to string
        ch.pipeline().addLast("decoder", new TcpStreamDecoder());

        // IdleStateHandler
        ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(this.readerIdleTimeSeconds,  this.writerIdleTimeSeconds, this.allIdleTimeSeconds));

        // default handler
        ch.pipeline().addLast("handler", this.heartbeatHandler);
    }
}
