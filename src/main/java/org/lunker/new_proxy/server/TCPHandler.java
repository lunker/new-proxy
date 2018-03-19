package org.lunker.new_proxy.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by dongqlee on 2018. 3. 15..
 */
public class TCPHandler extends ChannelInboundHandlerAdapter {

    private org.slf4j.Logger logger= LoggerFactory.getLogger(TCPHandler.class);
    private AtomicInteger count=new AtomicInteger(0);

    public TCPHandler() { }

    /**
     * asfsadasdf
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        logger.info(String.format("[%d]\n", count.incrementAndGet()));
        ByteBuf in = (ByteBuf) msg;
        try {
            String message="";

            while (in.isReadable()) { // (1)
                byte[] bytes = new byte[in.readableBytes()];
                in.readBytes(bytes);

                message=new String(bytes);
            }

            ctx.fireChannelRead(message);
        }
        finally {
            ReferenceCountUtil.release(msg); // (2)
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Client connected:: " + ctx.channel().remoteAddress().toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
        cause.printStackTrace();
//        ctx.close();
    }
}
