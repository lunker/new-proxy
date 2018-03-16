package org.lunker.new_proxy.server;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.SipFactory;
import javax.sip.message.MessageFactory;

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
        String message=(String) msg;
        SIPMessage sipMessage=stringMsgParser.parseSIPMessage(message.getBytes(), true, false,  null);

        if(sipMessage instanceof SIPResponse){
            // sip response
        }
        else{
            // sip request
        }

        ctx.fireChannelActive();
        ctx.fireChannelRead(sipMessage);

        /*
            SIPRequest sipRequest=(SIPRequest) messageFactory.createRequest(request);
            SIPResponse sipResponse=(SIPResponse) messageFactory.createResponse(Response.OK, sipRequest);
        */

    }
}
