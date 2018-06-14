package org.lunker.new_proxy.heartbeat;

import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.lunker.new_proxy.sip.util.SipMessageFactory;

import java.net.InetSocketAddress;

@ChannelHandler.Sharable
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private HeartbeatConnections heartbeatConnections;

    public HeartbeatHandler() {
        heartbeatConnections = HeartbeatConnections.getInstance();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        // remove connection
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        heartbeatConnections.remove(remoteAddress.getHostString(), remoteAddress.getPort());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // add connection
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        heartbeatConnections.add(remoteAddress.getHostString(), remoteAddress.getPort(), ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        // response if message type SIPRequest
//        if (msg instanceof SIPRequest) {
//            SIPResponse sipResponse = (SIPResponse) SipMessageFactory.getInstance().createResponse(200, (SIPRequest) msg);
//            ctx.channel().writeAndFlush((Unpooled.copiedBuffer(sipResponse.toString(), CharsetUtil.UTF_8)));
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
