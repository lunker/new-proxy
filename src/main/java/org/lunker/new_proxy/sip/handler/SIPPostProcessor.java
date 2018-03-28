package org.lunker.new_proxy.sip.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class SIPPostProcessor extends ChannelInboundHandlerAdapter{
    private Logger logger= LoggerFactory.getLogger(SIPPostProcessor.class);

    public SIPPostProcessor() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GeneralSipMessage generalSipMessage=(GeneralSipMessage) msg;

        generalSipMessage.send();

        if(generalSipMessage instanceof GeneralSipResponse){
            String method=generalSipMessage.getMethod();
        }
    }
}
