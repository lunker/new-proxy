package org.lunker.new_proxy.sip.processor.proxy;

import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.core.ProxyContext;
import org.lunker.new_proxy.sip.processor.PreProcessor;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipMessage;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipRequest;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipResponse;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.lunker.new_proxy.stub.session.ss.SipSession;
import org.lunker.new_proxy.util.lambda.StreamHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public class ProxyPreProcessor extends PreProcessor {
    private Logger logger= LoggerFactory.getLogger(ProxyPreProcessor.class);
    private StringMsgParser stringMsgParser=null;
    private ProxyContext proxyContext=ProxyContext.getInstance();

    Optional<SipMessageHandler> optionalSipMessageHandler=null;

    @Deprecated
    public ProxyPreProcessor() {

    }

    public ProxyPreProcessor(Optional<SipMessageHandler> optionalSipMessageHandler) {
        this.stringMsgParser=new StringMsgParser();
        this.optionalSipMessageHandler=optionalSipMessageHandler;
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            Optional<String> maybeStrSipMessage=(Optional<String>) msg;
            Optional<ProxySipMessage> maybeGeneralSipMessage=deserialize(ctx, maybeStrSipMessage);

            this.optionalSipMessageHandler.get().handle(maybeGeneralSipMessage);
        }
        catch (Exception e){
            logger.warn("Error while encoding sip wrapper . . . :\n" + ((Optional<String>) msg).get());
            ctx.fireExceptionCaught(e);
        }
    }

    private SIPMessage generateJainSipMessage(String strSipMessage) throws ParseException {
        return stringMsgParser.parseSIPMessage(strSipMessage.getBytes(), true, false, null);
    }

    private SIPMessage updateMessage(ChannelHandlerContext ctx, SIPMessage jainSipMessage) throws ParseException {
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

        return jainSipMessage;
    }

    private ProxySipMessage generateGeneralSipMessage(ChannelHandlerContext ctx, SIPMessage jainSipMessage){
        ProxySipMessage proxySipMessage =null;

        SipSession sipSession=proxyContext.createOrGetSIPSession(ctx, jainSipMessage);

        if(jainSipMessage instanceof SIPRequest){
            proxySipMessage =new ProxySipRequest(jainSipMessage, sipSession.getSipSessionkey());
        }
        else{
            proxySipMessage =new ProxySipResponse(jainSipMessage, sipSession.getSipSessionkey());
        }

        if(jainSipMessage instanceof SIPRequest && sipSession.getFirstRequest()==null && ((SIPRequest) jainSipMessage).getMethod().equals("INVITE")){
            sipSession.setFirstRequest((ProxySipRequest) proxySipMessage);
        }

        return proxySipMessage;
    }

    public Optional<ProxySipMessage> deserialize(ChannelHandlerContext ctx, Optional<String> maybeStrSipMessage) {
        return maybeStrSipMessage
                .map(StreamHelper.wrapper(strSipMessage -> generateJainSipMessage(strSipMessage)))
                .map(StreamHelper.wrapper(jainSipMessage -> updateMessage(ctx, jainSipMessage)))
                .map(jainSipMessage -> generateGeneralSipMessage(ctx, jainSipMessage));
    }
}
