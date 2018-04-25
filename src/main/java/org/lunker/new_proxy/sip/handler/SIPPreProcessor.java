package org.lunker.new_proxy.sip.handler;

import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.parser.SIPETagParser;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.sip.context.ProxyContext;
import org.lunker.new_proxy.sip.util.SipMessageFactory;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipRequest;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipResponse;
import org.lunker.new_proxy.stub.session.ss.SipSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.Optional;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class SIPPreProcessor extends ChannelInboundHandlerAdapter {

    private Logger logger= LoggerFactory.getLogger(SIPPreProcessor.class);
    private StringMsgParser stringMsgParser=null;
    private SipMessageFactory sipMessageFactory=null;
    private ProxyContext proxyContext=ProxyContext.getInstance();

    public SIPPreProcessor() {
        this.stringMsgParser=new StringMsgParser();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            Optional<String> maybeStrSipMessage=(Optional<String>) msg;

            /*
            maybeStrSipMessage.map((strSipMessage)->{
                Optional<SIPMessage> maybeJainSipMessage=null;

                maybeJainSipMessage=Optional.ofNullable(parseSipMessage(strSipMessage));
                return maybeJainSipMessage;
            });
            */

            maybeStrSipMessage.map(this::parseSipMessage);

            ctx.fireChannelActive();
            ctx.fireChannelRead(maybeStrSipMessage);
        }
        catch (Exception e){
            logger.warn("Error while encoding sip wrapper . . . :\n" + ((String) msg));
            ctx.fireExceptionCaught(e);
        }
    }

    public SIPMessage parseSipMessage(String strSipMessage){
        SIPMessage jainSipMessage=null;
        jainSipMessage=stringMsgParser.parseSIPMessage(strSipMessage.getBytes(), true, false, null);

        return jainSipMessage;
    }

    public GeneralSipMessage deserialize(ChannelHandlerContext ctx, Optional<String> maybeStrSipMessage) {
        GeneralSipMessage generalSipMessage=null;



        // TODO(lunker): message send를 위해 ctx를 session에 저장시킨다
        SipSession sipSession=proxyContext.createOrGetSIPSession(ctx, jainSipMessage);

        // update Via
        ViaList viaList=jainSipMessage.getViaHeaders();

        Via topViaHeader=(Via) viaList.getFirst();

        if (topViaHeader.getReceived() == null) {
            String received=((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
            topViaHeader.setReceived(received);
        }

        if(topViaHeader.getRPort() == 0 || topViaHeader.getRPort() == -1) {
            int rport=((InetSocketAddress) ctx.channel().remoteAddress()).getPort();

//            viaHeader.setParameter("rport", rport);
            topViaHeader.setParameter("rport", rport+"");

            /*
            try{
//                topViaHeader.setPort(rport);
                topViaHeader.setParameter("rport", rport+"");
            }
            catch (InvalidArgumentException iae){
                iae.printStackTrace();
            }
            */
        }

        viaList.set(0, topViaHeader);
        jainSipMessage.setHeader(viaList);

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
}
