package org.lunker.new_proxy.heartbeat;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import org.lunker.new_proxy.heartbeat.server.HeartbeatServer;

import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Optional;


public class HeartbeatServerTest {

    public HeartbeatServerTest() throws PeerUnavailableException {
    }

    public static void main(String[] args) throws InterruptedException, PeerUnavailableException {
        // create server channel
        HeartbeatServer heartbeatServer = new HeartbeatServer(new HeartbeatServerHandler());
        ChannelFuture serverChannel = heartbeatServer.run();
        serverChannel.channel().closeFuture().await();
    }

    @ChannelHandler.Sharable
    static class HeartbeatServerHandler extends HeartbeatHandler {
        MessageFactory messageFactory = SipFactory.getInstance().createMessageFactory();

        HeartbeatServerHandler() throws PeerUnavailableException {
        }

        public void pong(ChannelHandlerContext ctx, Request request) throws PeerUnavailableException, ParseException {
//            MessageFactory messageFactory = SipFactory.getInstance().createMessageFactory();
            Response response = messageFactory.createResponse(200, request);
            ctx.channel().writeAndFlush(Unpooled.copiedBuffer(response.toString(), CharsetUtil.UTF_8));
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Request request =  messageFactory.createRequest( ((Optional<String>)msg).get());
            System.out.println("Ping~\n"+request.toString());
            pong(ctx, request);
        }
    }


}
