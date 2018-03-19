package org.lunker.new_proxy.server;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.SipFactory;
import javax.sip.message.MessageFactory;
import java.text.ParseException;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class SIPMessageParser extends ChannelInboundHandlerAdapter {
    private Logger logger=LoggerFactory.getLogger(SIPMessageParser.class);
    private SipFactory sipFactory=null;
    private MessageFactory messageFactory=null;
    private StringMsgParser stringMsgParser=null;


    public SIPMessageParser() {
        try{
            sipFactory=javax.sip.SipFactory.getInstance();
            messageFactory=sipFactory.createMessageFactory();
            stringMsgParser=new StringMsgParser();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("In SIPMessageParser");

        /*
        ByteBuf in = (ByteBuf) msg;
        String message = "";
        try {
            while (in.isReadable()) { // (1)
                byte[] bytes = new byte[in.readableBytes()];
                in.readBytes(bytes);

                message = new String(bytes);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        */

        String message=(String) msg;
        message+="REGISTER sip:siptest.com SIP/2.0";
//        logger.info(message);

        try{

            // 단순히 string message를 SIPMessage 객체로 변환시켜줄 뿐, error-format에 대해서 exception을 발생시키거나 하지 않는다...
            SIPMessage sipMessage=stringMsgParser.parseSIPMessage(message.getBytes(), true, false,  null);
            ctx.fireChannelActive();
            ctx.fireChannelRead(sipMessage);
        }
        catch (ParseException pe){
            pe.printStackTrace();
            logger.error("Parsing error. Original message -> \n" + message);

            ctx.fireExceptionCaught(pe);
            ctx.flush();
//            ctx.close(); // ?
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
//        super.exceptionCaught(ctx, cause);
    }
}
