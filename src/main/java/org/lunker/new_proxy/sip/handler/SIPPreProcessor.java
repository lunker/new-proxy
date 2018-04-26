package org.lunker.new_proxy.sip.handler;

import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.sip.context.ProxyContext;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipRequest;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipResponse;
import org.lunker.new_proxy.stub.SipServlet;
import org.lunker.new_proxy.stub.session.ss.SipSession;
import org.lunker.new_proxy.util.lambda.StreamHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;


/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class SIPPreProcessor extends ChannelInboundHandlerAdapter {

    private Logger logger= LoggerFactory.getLogger(SIPPreProcessor.class);
    private StringMsgParser stringMsgParser=null;
    private ProxyContext proxyContext=ProxyContext.getInstance();
    private List<Optional<SipServlet>> handlers=null;

    public SIPPreProcessor() {
        this.stringMsgParser=new StringMsgParser();
    }

    public SIPPreProcessor(List handlers){
        this();
        this.handlers=handlers;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            Optional<String> maybeStrSipMessage=(Optional<String>) msg;
            Optional<GeneralSipMessage> maybeGeneralSipMessage=deserialize(ctx, maybeStrSipMessage);

            handlers.forEach((handler)->{
                handler.get().handle(maybeGeneralSipMessage);
            });
        }
        catch (Exception e){
            logger.warn("Error while encoding sip wrapper . . . :\n" + ((String) msg));
            ctx.fireExceptionCaught(e);
        }
    }

    /*
    private Optional<SIPMessage> generateJainSipMessage(String strSipMessage) {
        Optional<String> maybeStrSipMessage=Optional.of(strSipMessage);

        return maybeStrSipMessage.map(StreamHelper.wrapper(strSipMessageParam -> stringMsgParser.parseSIPMessage(strSipMessage.getBytes(), true, false, null)));
    }

    private Optional<SIPMessage> updateMessage(ChannelHandlerContext ctx, Optional<SIPMessage> maybeJainSipMessage) {

        return maybeJainSipMessage.map(StreamHelper.wrapper((jainSipMessage)->{

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
        }));
    }

    private Optional<GeneralSipMessage> generateGeneralSipMessage(ChannelHandlerContext ctx, Optional<SIPMessage> maybeJainSipMessage){

        return maybeJainSipMessage.map(StreamHelper.wrapper((jainSipMessage)-> {

            GeneralSipMessage generalSipMessage=null;

            SipSession sipSession=proxyContext.createOrGetSIPSession(ctx, jainSipMessage);

            if(jainSipMessage instanceof SIPRequest){
                generalSipMessage=new GeneralSipRequest(jainSipMessage, sipSession.getSipSessionkey());
            }
            else{
                generalSipMessage=new GeneralSipResponse(jainSipMessage, sipSession.getSipSessionkey());
            }

            if(jainSipMessage instanceof SIPRequest && sipSession.getFirstRequest()==null && ((SIPRequest) jainSipMessage).getMethod().equals("INVITE")){
                sipSession.setFirstRequest((GeneralSipRequest) generalSipMessage);
            }

            return generalSipMessage;
        }));
    }
    */

    private SIPMessage generateJainSipMessage(String strSipMessage) throws ParseException{
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

    private GeneralSipMessage generateGeneralSipMessage(ChannelHandlerContext ctx, SIPMessage jainSipMessage){
        GeneralSipMessage generalSipMessage=null;

        SipSession sipSession=proxyContext.createOrGetSIPSession(ctx, jainSipMessage);

        if(jainSipMessage instanceof SIPRequest){
            generalSipMessage=new GeneralSipRequest(jainSipMessage, sipSession.getSipSessionkey());
        }
        else{
            generalSipMessage=new GeneralSipResponse(jainSipMessage, sipSession.getSipSessionkey());
        }

        if(jainSipMessage instanceof SIPRequest && sipSession.getFirstRequest()==null && ((SIPRequest) jainSipMessage).getMethod().equals("INVITE")){
            sipSession.setFirstRequest((GeneralSipRequest) generalSipMessage);
        }

        return generalSipMessage;
    }

    public Optional<GeneralSipMessage> deserialize(ChannelHandlerContext ctx, Optional<String> maybeStrSipMessage) {
        return maybeStrSipMessage
                .map(StreamHelper.wrapper(strSipMessage -> generateJainSipMessage(strSipMessage)))
                .map(StreamHelper.wrapper(jainSipMessage -> updateMessage(ctx, jainSipMessage)))
                .map(jainSipMessage -> generateGeneralSipMessage(ctx, jainSipMessage) );
    }
}
