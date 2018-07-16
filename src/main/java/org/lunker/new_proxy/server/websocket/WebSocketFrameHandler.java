package org.lunker.new_proxy.server.websocket;

import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.lunker.new_proxy.core.ConnectionManager;
import org.lunker.new_proxy.core.constants.ServerType;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.lb.LoadBalancerRequest;
import org.lunker.new_proxy.sip.wrapper.message.lb.LoadBalancerResponse;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipRequest;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipResponse;
import org.lunker.new_proxy.util.lambda.StreamHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.Optional;

import static org.lunker.new_proxy.core.constants.ServerType.LB;
import static org.lunker.new_proxy.core.constants.ServerType.PROXY;

/**
 * Created by dongqlee on 2018. 5. 30..
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private Logger logger= LoggerFactory.getLogger(WebSocketFrameHandler.class);
    private StringMsgParser stringMsgParser;

    public WebSocketFrameHandler() {
        this.stringMsgParser = new StringMsgParser();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress remoteAddress=((InetSocketAddress)ctx.channel().remoteAddress());
        ConnectionManager.getInstance().addConnection(remoteAddress.getHostString(), remoteAddress.getPort(), Transport.WSS.getValue(), ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        InetSocketAddress remoteAddress=((InetSocketAddress)ctx.channel().remoteAddress());
        ConnectionManager.getInstance().deleteConnection(remoteAddress.getHostString(), remoteAddress.getPort(), Transport.TCP.getValue());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        if (msg instanceof TextWebSocketFrame) {
            // Send the uppercase string back.
            String request = ((TextWebSocketFrame) msg).text();
            logger.info("{} received\n{}", ctx.channel(), request);
            Optional<DefaultSipMessage> maybeGeneralSipMessage=deserialize(ctx, Optional.ofNullable(request));
//            ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
//            ctx.fireChannelRead(Optional.ofNullable(request));
            ctx.fireChannelRead(maybeGeneralSipMessage);

        } else {
            String message = "unsupported frame type: " + msg.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    private SIPMessage generateJainSipMessage(String strSipMessage) throws ParseException {
        return stringMsgParser.parseSIPMessage(strSipMessage.getBytes(), true, false, null);
    }

    /**
     * Set ServerReflexive address to Via 'rport', 'received'
     * @param ctx
     * @param jainSipMessage
     * @return
     * @throws ParseException
     */
    private SIPMessage updateMessage(ChannelHandlerContext ctx, SIPMessage jainSipMessage) throws ParseException {
        if(jainSipMessage instanceof SIPRequest){
            ViaList viaList=jainSipMessage.getViaHeaders();

            Via topViaHeader=(Via) viaList.getFirst();

            if (topViaHeader.getReceived() == null) {
                String received=((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
                topViaHeader.setReceived(received);
            }

            if(topViaHeader.getRPort() == 0 || topViaHeader.getRPort() == -1) {
                int rport=((InetSocketAddress) ctx.channel().remoteAddress()).getPort();

                topViaHeader.setParameter("rport", rport+"");
            }

            viaList.set(0, topViaHeader);
            jainSipMessage.setHeader(viaList);
        }

        return jainSipMessage;
    }

    private DefaultSipMessage generateGeneralSipMessage(ChannelHandlerContext ctx, SIPMessage jainSipMessage){
        DefaultSipMessage defaultSipMessage =null;
        // TODO: server type
        if(jainSipMessage instanceof SIPRequest){
            switch (ServerType.PROXY) {
                case PROXY:
                    defaultSipMessage = new ProxySipRequest(jainSipMessage);
                    break;
                case LB:
                    defaultSipMessage = new LoadBalancerRequest(jainSipMessage);
                    break;
            }
        }
        else{ // jainSipMessage instanceof SIPResponse
            switch (ServerType.PROXY) {
                case PROXY:
                    defaultSipMessage = new ProxySipResponse(jainSipMessage);
                    break;
                case LB:
                    defaultSipMessage = new LoadBalancerResponse(jainSipMessage);
                    break;
            }
        }

        /*
        // TODO: next step on 'Stateful Proxy'
        if(jainSipMessage instanceof SIPRequest && sipSession.getFirstRequest()==null && ((SIPRequest) jainSipMessage).getMethod().equals("INVITE")){
            sipSession.setFirstRequest((ProxySipRequest) proxySipMessage);
        }
        */

        return defaultSipMessage;
    }

    public Optional<DefaultSipMessage> deserialize(ChannelHandlerContext ctx, Optional<String> maybeStrSipMessage) {
        return maybeStrSipMessage
                .map(StreamHelper.wrapper(strSipMessage->generateJainSipMessage(strSipMessage)))
                .map(StreamHelper.wrapper(jainSipMessage->updateMessage(ctx, jainSipMessage)))
                .map(jainSipMessage->generateGeneralSipMessage(ctx, jainSipMessage));
    }
}
