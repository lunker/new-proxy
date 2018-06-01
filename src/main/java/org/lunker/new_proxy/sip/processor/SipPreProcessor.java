package org.lunker.new_proxy.sip.processor;

import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.core.ConnectionManager;
import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipRequest;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipResponse;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.lunker.new_proxy.util.lambda.StreamHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.Optional;

/**
 * Created by dongqlee on 2018. 5. 30..
 */
public class SipPreProcessor extends ChannelInboundHandlerAdapter {
    private Logger logger= LoggerFactory.getLogger(SipPreProcessor.class);
    private ConnectionManager connectionManager=ConnectionManager.getInstance();
    protected ServerInfo serverInfo=null;

    private StringMsgParser stringMsgParser=null;
    Optional<SipMessageHandler> optionalSipMessageHandler=null;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    }

    // TODO: save user connection using ip, port, transport
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress remoteAddress=((InetSocketAddress)ctx.channel().remoteAddress());

        this.connectionManager.addConnection(remoteAddress.getHostString(), remoteAddress.getPort(),"tcp", ctx);
    }

    // TODO: Refactoring. Proxy, LB preprocessor를 분리 및 상속 받을 필요가 없다
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            Mono<String> wrapper=Mono.fromCallable(()->{
                Optional<String> maybeStrSipMessage=(Optional<String>) msg;

                // 결국 이것만 다르다
                Optional<DefaultSipMessage> maybeGeneralSipMessage=deserialize(ctx, maybeStrSipMessage);

                this.optionalSipMessageHandler.get().handle(maybeGeneralSipMessage);

                return "fromCallable return value";
            });

            wrapper=wrapper.subscribeOn(Schedulers.parallel());
            wrapper.subscribe();
        }
        catch (Exception e){
            logger.warn("Error while encoding sip wrapper . . . :\n" + ((Optional<String>) msg).get());
            ctx.fireExceptionCaught(e);
        }
    }

    private SIPMessage generateJainSipMessage(String strSipMessage) throws ParseException {
        return stringMsgParser.parseSIPMessage(strSipMessage.getBytes(), true, false, null);
    }


    // TODO: Refactoring -> 'proxy' app으로 이동
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

//        SipSession sipSession=proxyContext.createOrGetSIPSession(ctx, jainSipMessage);

        if(jainSipMessage instanceof SIPRequest){
            // TODO: create ProxySipMessage with SipSession
            defaultSipMessage=new ProxySipRequest(jainSipMessage);
        }
        else{
            defaultSipMessage=new ProxySipResponse(jainSipMessage);
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
                .map(StreamHelper.wrapper(strSipMessage -> generateJainSipMessage(strSipMessage)))
                .map(StreamHelper.wrapper(jainSipMessage -> updateMessage(ctx, jainSipMessage)))
                .map(jainSipMessage -> generateGeneralSipMessage(ctx, jainSipMessage));
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        logger.info("channelInactive");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//        logger.info("channelUnregistered");
        InetSocketAddress remoteAddress=((InetSocketAddress)ctx.channel().remoteAddress());

        this.connectionManager.deleteConnection(remoteAddress.getHostString(), remoteAddress.getPort(),"tcp");
    }
}
