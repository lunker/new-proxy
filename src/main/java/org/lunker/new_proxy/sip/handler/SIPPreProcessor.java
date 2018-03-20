package org.lunker.new_proxy.sip.handler;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.sip.context.ProxyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class SIPPreProcessor extends ChannelInboundHandlerAdapter {

    private Logger logger= LoggerFactory.getLogger(SIPPreProcessor.class);
    private StringMsgParser stringMsgParser=null;
    private ProxyContext proxyContext=ProxyContext.getInstance();

    public SIPPreProcessor() {
        stringMsgParser=new StringMsgParser();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            SIPMessage sipMessage=null;
            String toTag="";
            String fromTag="";
            String callId="";

            sipMessage=stringMsgParser.parseSIPMessage( ((String)msg).getBytes(), true, false,  null);

//
//            sipMessage.getFromHeader().getTag();
//            SIPSession sipSession= new SIPSessionImpl()


            ctx.fireChannelActive();
            ctx.fireChannelRead(sipMessage);
        }
        catch (Exception e){
            logger.warn("Error while encoding sip message . . . :\n" + ((String) msg));
            ctx.fireExceptionCaught(e);
        }
    }
}
