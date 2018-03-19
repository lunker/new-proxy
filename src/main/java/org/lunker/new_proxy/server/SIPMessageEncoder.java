package org.lunker.new_proxy.server;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class SIPMessageEncoder extends ChannelInboundHandlerAdapter {

    private Logger logger= LoggerFactory.getLogger(SIPMessageEncoder.class);
    private StringMsgParser stringMsgParser=null;

    public SIPMessageEncoder() {
        stringMsgParser=new StringMsgParser();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);

        try{
            SIPMessage sipMessage=stringMsgParser.parseSIPMessage( ((String)msg).getBytes(), true, false,  null);
            ctx.fireChannelActive();
            ctx.fireChannelRead(sipMessage);
        }
        catch (Exception e){
            logger.warn("Error while encoding sip message . . . :\n" + ((String) msg));
            ctx.fireExceptionCaught(e);
        }
    }
}
