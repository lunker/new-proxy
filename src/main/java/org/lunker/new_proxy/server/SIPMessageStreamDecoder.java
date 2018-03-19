package org.lunker.new_proxy.server;

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
            logger.info("Received raw buffer : " + buf.toString(CharsetUtil.UTF_8));
        }

        /*

        int readLength=0;
        readLength=buf.readableBytes();

        byte[] readByteArr=new byte[readLength];

        buf.readBytes(readByteArr);

        if(readLength==2 && readByteArr[readLength-2] == 13 && readByteArr[readLength-1] == 10){
            logger.info("CRLF!");
            return null;
        }
        */

        String sipMsgLine=buf.toString(CharsetUtil.UTF_8);
//        String sipMsgLine=new String(readByteArr);

        if(sipMsgLine.equals("\r\n")){
            return null;
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
                /*
                ByteBuf data = (ByteBuf) msg;
                first = cumulation == null;
                if (first) {
                    cumulation = data;
                } else {
                    cumulation = cumulator.cumulate(ctx.alloc(), cumulation, data);
                }
                 */

                int readbytes=0;
                byte[] contentBody=new byte[contentLengthValue];

                if(buffer.isReadable()){
                    buffer.readBytes(contentBody);
                }

                sipMessageBuilder.append(new String(contentBody));
                sipMessageBuilder.append("\r\n");
                String rawSipMessage=sipMessageBuilder.toString();
                sipMessageBuilder=new StringBuilder();

                return rawSipMessage;
            }
            else{
                // just fire ctx event
                String rawSipMessage=sipMessageBuilder.toString();
                sipMessageBuilder=new StringBuilder();

                return rawSipMessage;
            }
        }

        return null;
    }

    /*
    boolean isPreviousLineCRLF = false;
		while(true) { // We read continiously from the bytes we receive and only break where there are no more bytes in the inputStream passed to us
			if(currentStreamEnded) break; // The stream ends when we have read all bytes in the chunk NIO passed to us
			else {
				if(readingHeaderLines) {// We are in state to read header lines right now
					isPreviousLineCRLF = readMessageSipHeaderLines(inputStream, isPreviousLineCRLF);
				}
				if(readingMessageBodyContents) { // We've already read the headers an now we are reading the Contents of the SIP message (which doesn't generally have lines)
					readMessageBody(inputStream);
				}
			}
		}
     */
}
