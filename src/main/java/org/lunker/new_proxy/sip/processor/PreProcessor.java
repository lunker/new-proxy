package org.lunker.new_proxy.sip.processor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.core.ConnectionManager;
import org.lunker.new_proxy.model.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
//@ChannelHandler.Sharable
public abstract class PreProcessor extends ChannelInboundHandlerAdapter {
    private Logger logger= LoggerFactory.getLogger(PreProcessor.class);
    private ConnectionManager connectionManager=ConnectionManager.getInstance();
    protected ServerInfo serverInfo=null;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        logger.info("channelRegistered");
    }

    // TODO: save user connection using ip, port, transport
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        logger.info("channelactive");
        InetSocketAddress remoteAddress=((InetSocketAddress)ctx.channel().remoteAddress());

        this.connectionManager.addClient(remoteAddress.getHostString(), remoteAddress.getPort(),this.serverInfo.getTransport().getValue(), ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        logger.info("channelInactive");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//        logger.info("channelUnregistered");
        InetSocketAddress remoteAddress=((InetSocketAddress)ctx.channel().remoteAddress());

        this.connectionManager.deleteClient(remoteAddress.getHostString(), remoteAddress.getPort(),"tcp");
    }
}
