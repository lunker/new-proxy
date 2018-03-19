package org.lunker.new_proxy.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class SIPByteDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        while(in.isReadable()){
            byte b=in.readByte();

        }

    }
}
