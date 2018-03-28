package org.lunker.new_proxy.sip.handler;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
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

import java.text.ParseException;

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
//        this.sipMessageFactory=new SipMessageFactory();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            GeneralSipMessage generalSipMessage=deserialize(ctx, (String) msg);

            ctx.fireChannelActive();
            ctx.fireChannelRead(generalSipMessage);
        }
        catch (Exception e){
            logger.warn("Error while encoding sip wrapper . . . :\n" + ((String) msg));
            ctx.fireExceptionCaught(e);
        }
    }

    public GeneralSipMessage deserialize(ChannelHandlerContext ctx, String message) throws ParseException{
        GeneralSipMessage generalSipMessage=null;

        SIPMessage jainSipMessage=stringMsgParser.parseSIPMessage(message.getBytes(), true, false, null);

        // TODO(lunker): message send를 위해 ctx를 session에 저장시킨다
        SipSession sipSession=proxyContext.createOrGetSIPSession(ctx, jainSipMessage);

        if(jainSipMessage instanceof SIPRequest){
            generalSipMessage=new GeneralSipRequest(jainSipMessage, sipSession.getSipSessionkey());
        }
        else{
            generalSipMessage=new GeneralSipResponse(jainSipMessage, sipSession.getSipSessionkey());
        }

        return generalSipMessage;
    }
}
