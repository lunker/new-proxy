package org.lunker.new_proxy.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class LoggingHandler extends ChannelInboundHandlerAdapter {
    private Logger logger= LoggerFactory.getLogger(LoggingHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("In LoggingHandler");

        ChannelFuture cf = ctx.write(Unpooled.copiedBuffer(msg.toString(), CharsetUtil.UTF_8));
        ctx.flush();
        if (!cf.isSuccess()) {
            logger.warn("Send failed: " + cf.cause());
        }

        logger.info("[SENT]:\n" + (String) msg);
    }
}
