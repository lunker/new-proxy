package org.lunker.new_proxy.client.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.core.ConnectionManager;
import org.lunker.new_proxy.model.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class TcpClientHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(TcpClientHandler.class);
    private ConnectionManager connectionManager = ConnectionManager.getInstance();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        // register channel to connection manager
        InetSocketAddress remoteAddress = ((InetSocketAddress)ctx.channel().remoteAddress());
        this.connectionManager.addConnection(
                remoteAddress.getHostString(),
                remoteAddress.getPort(),
                Transport.TCP.getValue(),
                ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        // unregister channel from connection manager
        InetSocketAddress remoteAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        this.connectionManager.deleteConnection(remoteAddress.getHostString(),
                remoteAddress.getPort(),
                Transport.TCP.getValue());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // TODO: send message?
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        // TODO: ??
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // TODO: read message

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

}
