package org.lunker.new_proxy.sip.handler;

import gov.nist.javax.sip.header.ContentLength;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class SIPMessageStreamDecoder extends LineBasedFrameDecoder {

    private Logger logger= LoggerFactory.getLogger(SIPMessageStreamDecoder.class);

    private int INITIAL_BUFFER=1024;

    private StringBuilder sipMessageBuilder=null;

    private int test=0;
    private boolean isCRLF=false;
    private boolean hasContentBody=false;


    private byte[] preservedBuffer=null;


    public SIPMessageStreamDecoder(int maxLength) {
        super(maxLength); // ?

        sipMessageBuilder=new StringBuilder();
        logger.info("Test count: " + test++);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {

        logger.debug("[SIPMessageStreamDecoder][decode()]");

        ByteBuf buf=(ByteBuf) super.decode(ctx, buffer);
        if(buf == null){
            logger.info("null buffer");
            return null;
        }
        else{
//            logger.info("Received raw buffer : " + buf.toString(CharsetUtil.UTF_8));
        }

        String sipMsgLine=buf.toString(CharsetUtil.UTF_8);

        if(sipMsgLine.equals("\r\n")){
            return null;
        }

        if(preservedBuffer!=null){
            sipMessageBuilder.append(new String(preservedBuffer));
            preservedBuffer=null;
        }

        sipMessageBuilder.append(sipMsgLine);
        sipMessageBuilder.append("\r\n");

        if(sipMsgLine.toLowerCase().startsWith("content-length")){
            // find content length
            logger.info("Found Content-Length Header!");

            // get content-length value
            int contentLengthValue=Integer.parseInt(sipMsgLine.substring(ContentLength.NAME_LOWER.length()+1).trim());

            if(contentLengthValue!=0){
                // read addtional buffer (message body)

                byte[] contentBody=null;

                try{
                    if(buffer.isReadable() && contentLengthValue  <= buffer.readableBytes()){
                        contentBody=new byte[contentLengthValue];

                        buffer.readBytes(contentBody);
                    }
                    else{
                        logger.info("why here?");

                        preservedBuffer=new byte[buffer.readableBytes()];

                        buffer.readBytes(preservedBuffer);
                        return null;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                if(contentBody!=null){
                    sipMessageBuilder.append(new String(contentBody));
                    sipMessageBuilder.append("\r\n");

                    String rawSipMessage=sipMessageBuilder.toString();
                    sipMessageBuilder=new StringBuilder();

                    return rawSipMessage;
                }
                else{
                    return null;
                }
            }
            else{
                // just fire ctx event
                String rawSipMessage=sipMessageBuilder.toString();
                sipMessageBuilder=new StringBuilder();
//                buf.release();


                return rawSipMessage;
            }
        }

        return null;
    }
}
