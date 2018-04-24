package org.lunker.new_proxy.sip.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.ContentLengthHeader;
import java.util.List;

/**
 * Created by dongqlee on 2018. 3. 22..
 */
public class SIPStreamDecoder extends ByteToMessageDecoder{


    private Logger logger= LoggerFactory.getLogger(SIPStreamDecoder.class);

    private final int DEFAULT_HEADER_SIZE=2500;
    private final int DEFAULT_HEADER_LINE_SIZE=512;

    private ByteBuf headerBuffer=null;
    private ByteBuf headerLineBuffer=null;
    private int headerIdx=0;
    private ByteBuf bodyBuffer=null;
    private int bodyIdx=0;

    private final int CR=13;
    private final int LF=10;
    private PooledByteBufAllocator pooledByteBufAllocator=null;
    private UnpooledByteBufAllocator unpooledByteBufAllocator=null;

    private boolean isHeaderState=true;
    private boolean isBodyState=false;
    private byte lastByte;
    private boolean hasCRLF=false;
    private int lastHeaerIdx=0;
    private int contentLength=-1;
    private int readBodyLength=0;
    private byte[] contentByte="Content-Length:".getBytes();

    public SIPStreamDecoder() {
        pooledByteBufAllocator=new PooledByteBufAllocator();
        unpooledByteBufAllocator=new UnpooledByteBufAllocator(false);

        headerBuffer=allocate(DEFAULT_HEADER_SIZE);
        headerLineBuffer=allocate(DEFAULT_HEADER_LINE_SIZE);
    }

    public ByteBuf allocate(int size){
        return unpooledByteBufAllocator.buffer(size);
    }

    public boolean contains(ByteBuf srcBuf, byte[] targetBuf){
        byte[] srcTmp=srcBuf.array();
        byte[] small;
        byte[] large;

        if(srcTmp.length > targetBuf.length){
            small=targetBuf;
            large=srcTmp;
        }
        else{
            small=srcTmp;
            large=targetBuf;
        }

        for(int idx=0; idx<small.length; idx++){
            if(small[idx] != large[idx])
                return false;
        }

        return true;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte currentByte;

        while(in.isReadable()){
            currentByte=in.readByte();

            if(this.isHeaderState){
                // read Header
                if(currentByte != CR && currentByte != LF){
                    headerLineBuffer.writeByte(currentByte);
                    this.hasCRLF=false;
                }
                else if(currentByte == CR){
                    // ignore

                    if(!this.hasCRLF && contains(headerLineBuffer, contentByte)){

                        try{
                            contentLength=Integer.parseInt(headerLineBuffer.toString(CharsetUtil.UTF_8).substring(
                                    ContentLengthHeader.NAME.length()+1).trim());

//                            bodyBuffer=pooledByteBufAllocator.directBuffer(contentLength);
                            bodyBuffer=allocate(contentLength);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
                else if(currentByte == LF){
                    if (!this.hasCRLF && this.lastByte == CR){
                        this.hasCRLF=true;

                        headerLineBuffer.writeByte(CR);
                        headerLineBuffer.writeByte(LF);


                        headerBuffer.writeBytes(headerLineBuffer);
                        headerLineBuffer.clear();
                    }
                    else if(this.hasCRLF==true && this.lastByte == CR){
                        // end of header! need to parsing Content-Length Value
                        headerBuffer.writeByte(CR);
                        headerBuffer.writeByte(LF);

                        this.hasCRLF=false;

                        // change state
                        this.isHeaderState=false;
                        this.isBodyState=true;
                    }

                }// end handling
            }

            if(this.isBodyState){
                // read content body

                if(this.contentLength > this.readBodyLength){
                    this.bodyBuffer.writeByte(currentByte);
                    readBodyLength++;
                }
                else{

                    // create entire Sip wrapper
                    try{

                        String sipMessage=headerBuffer.toString(CharsetUtil.UTF_8) + bodyBuffer.toString(CharsetUtil.UTF_8);
                        logger.info("Parsed sip wrapper:\n" + sipMessage);

                        // reset used buffer
                        headerBuffer.clear();
                        bodyBuffer.clear();
                        headerLineBuffer.clear();

                        // change state
                        this.isHeaderState=true;
                        this.isBodyState=false;
                        this.readBodyLength=0;
                        this.contentLength=0;

                        /*
                        this.headerBuffer.release();
                        this.bodyBuffer.release();
                        this.headerLineBuffer.release();
                        */

//                        this.headerBuffer=allocate(DEFAULT_HEADER_SIZE);
//                        this.headerLineBuffer=allocate(DEFAULT_HEADER_LINE_SIZE);

                        ctx.fireChannelRead(sipMessage);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            this.lastByte=currentByte;
        }
    }

    /**
     * Detect isDoubleCRLF
     * @return
     */
    public boolean isDoubleCRLF(){
        return false;
    }
}
