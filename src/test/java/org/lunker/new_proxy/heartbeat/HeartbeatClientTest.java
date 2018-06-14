package org.lunker.new_proxy.heartbeat;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.lunker.new_proxy.heartbeat.client.HeartbeatClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Optional;

public class HeartbeatClientTest {

    public static void main(String[] args) throws InterruptedException, PeerUnavailableException {
        // create client channel
//        Mono<ChannelFuture> clientThread = Mono.fromCallable(()-> {
            HeartbeatClient heartbeatClient = new HeartbeatClient(new HeartbeatClientHandler());
            ChannelFuture clientChannel = heartbeatClient.connect("127.0.0.1", 2000);
//            return clientChannel;
//        });
//        clientThread.subscribeOn(Schedulers.newElastic("elastic-tcp-client"));
//        clientThread.subscribe((channelFuture) -> {
//            while(true) {
//                HeartbeatConnections.getInstance().get("127.0.0.1", 2000).fireUserEventTriggered("ping");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        clientChannel.channel().closeFuture().await();
    }

    @ChannelHandler.Sharable
    static class HeartbeatClientHandler extends HeartbeatHandler {
        MessageFactory messageFactory = SipFactory.getInstance().createMessageFactory();

        HeartbeatClientHandler() throws PeerUnavailableException {
        }

        public void ping(ChannelHandlerContext ctx) throws ParseException, PeerUnavailableException {
//            MessageFactory messageFactory = SipFactory.getInstance().createMessageFactory();
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

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Response response = (Response) messageFactory.createResponse(((Optional<String>)msg).get());
//            super.channelRead(ctx, );
            System.out.println("Pong~\n"+response.toString());
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent ise = (IdleStateEvent) evt;
                switch (ise.state()) {
                    case READER_IDLE:
                        System.err.println("Reader idle");
                        break;
                    case WRITER_IDLE:
                        ping(ctx);
                        break;
                    case ALL_IDLE:
                        System.err.println("all idle");
                        break;
                }
            }
            super.userEventTriggered(ctx, evt);
//            ping(ctx);
//            Thread.sleep(1000);
        }
    }
}
