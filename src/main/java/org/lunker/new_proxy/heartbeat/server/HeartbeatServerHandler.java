package org.lunker.new_proxy.heartbeat.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.lunker.new_proxy.heartbeat.HeartbeatHandler;
import org.lunker.new_proxy.sip.util.SipMessageFactory;

import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Optional;

@ChannelHandler.Sharable
public class HeartbeatServerHandler extends HeartbeatHandler {
    private MessageFactory messageFactory = SipMessageFactory.getInstance().getMessageFactory();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        // TODO: do response about to request
        Request request = messageFactory.createRequest(((Optional<String>) msg).get());
        pong(ctx, request);
        // TODO: update timestamp if needed.
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent ise = (IdleStateEvent) evt;
            switch (ise.state()) {
                case READER_IDLE:
                    // TODO: timeout. remove client here?
                    break;
                case WRITER_IDLE:
                    break;
                case ALL_IDLE:
                    break;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    public void pong(ChannelHandlerContext ctx, Request request) throws ParseException {
        Response response = messageFactory.createResponse(Response.OK, request);
        ctx.channel().writeAndFlush(Unpooled.copiedBuffer(response.toString(), CharsetUtil.UTF_8));
    }
}
