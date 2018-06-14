package org.lunker.new_proxy.heartbeat.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.lunker.new_proxy.heartbeat.HeartbeatHandler;
import org.lunker.new_proxy.sip.util.SipMessageFactory;

import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import java.text.ParseException;

@ChannelHandler.Sharable
public class HeartbeatClientHandler extends HeartbeatHandler {
    private MessageFactory messageFactory = SipMessageFactory.getInstance().getMessageFactory();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // TODO: something do here if needed.
        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent ise = (IdleStateEvent) evt;
            switch (ise.state()) {
                case READER_IDLE:
                    break;
                case WRITER_IDLE:
                    // TODO: add argument about server info
                    ping(ctx);
                    break;
                case ALL_IDLE:
                    break;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    public void ping(ChannelHandlerContext ctx) throws ParseException {
        // TODO: this code is sample. required real message. add parameter to method if needed.
        Request request = messageFactory.createRequest(
                "INFO sip:alice@pc33.example.com SIP/2.0\n" +
                        "Via: SIP/2.0/UDP 192.0.2.2:5060;branch=z9hG4bKnabcdef\n" +
                        "To: Bob <sip:bob@example.com>;tag=a6c85cf\n" +
                        "From: Alice <sip:alice@example.com>;tag=1928301774\n" +
                        "Call-Id: a84b4c76e66710@pc33.example.com\n" +
                        "CSeq: 314333 INFO\n" +
                        "Info-Package: foo\n" +
                        "Content-type: application/foo\n" +
                        "Content-Disposition: Info-Package\n" +
                        "Content-length: 23\n" +
                        "\n" +
                        "I am a foo message type"
        );
        ctx.channel().writeAndFlush(Unpooled.copiedBuffer(request.toString(), CharsetUtil.UTF_8));
    }
}
