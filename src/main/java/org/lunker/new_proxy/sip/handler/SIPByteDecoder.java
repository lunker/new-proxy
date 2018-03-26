package org.lunker.new_proxy.sip.handler;

import gov.nist.javax.sip.parser.MessageParser;
import gov.nist.javax.sip.parser.StringMsgParser;
import gov.nist.javax.sip.stack.SIPTransactionStack;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.CallIdHeader;
import javax.sip.header.ContentLengthHeader;
import java.io.IOException;
import java.util.List;

/**
 * Created by dongqlee on 2018. 3. 22..
 */
public class SIPByteDecoder extends ByteToMessageDecoder{
    private Logger logger= LoggerFactory.getLogger(SIPByteDecoder.class);
    private static final String CRLF = "\r\n";
    private int maxMessageSize=0;
    private int sizeCounter;
    private SIPTransactionStack sipStack;
    private MessageParser smp = null;
    boolean isRunning = false;
    boolean currentStreamEnded = false;
    boolean readingMessageBodyContents = false;
    boolean readingHeaderLines = true;
    boolean partialLineRead = false; // if we didn't receive enough bytes for a full line we expect the line to end in the next batch of bytes
    String partialLine = "";
    String callId;

    StringBuffer message = new StringBuffer();
    byte[] messageBody = null;
    int contentLength = 0;
    int contentReadSoFar = 0;

    private ChannelHandlerContext ctx;

    private ByteBuf preservedBuff=null;

    public SIPByteDecoder() {
        smp=new StringMsgParser();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        logger.info("In decode");
        currentStreamEnded=false;

        ByteBuf buffer=null;

        if(preservedBuff!=null){
            int size=preservedBuff.readableBytes() + in.readableBytes();
            PooledByteBufAllocator a=new PooledByteBufAllocator();

            buffer=a.directBuffer(size);
            buffer.writeBytes(preservedBuff, preservedBuff.readableBytes());
            buffer.writeBytes(in, in.readableBytes());
            preservedBuff=null;
            logger.info(String.format("Concat preservedBuff with contentLength : %d", contentLength));
        }
        else{
            buffer=(ByteBuf) in;
            logger.info(String.format("No preservedBuff with contentLength : %d", contentLength));
        }

        readStream(buffer);
    }

    private void readStream(ByteBuf inputStream) throws IOException {
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
    }

    private boolean readMessageSipHeaderLines(ByteBuf inputStream, boolean isPreviousLineCRLF) throws IOException {
        boolean crlfReceived = false;
        String line = readLine(inputStream); // This gives us a full line or if it didn't fit in the byte check it may give us part of the line
        if(partialLineRead) {
            partialLine = partialLine + line; // If we are reading partial line again we must concatenate it with the previous partial line to reconstruct the full line
        } else {
            line = partialLine + line; // If we reach the end of the line in this chunk we concatenate it with the partial line from the previous buffer to have a full line
            partialLine = ""; // Reset the partial line so next time we will concatenate empty string instead of the obsolete partial line that we just took care of
            if(!line.equals(CRLF)) { // CRLF indicates END of message headers by RFC
                message.append(line); // Collect the line so far in the message buffer (line by line)
                String lineIgnoreCase = line.toLowerCase();
                // contribution from Alexander Saveliev compare to lower case as RFC 3261 states (7.3.1 Header Field Format) states that header fields are case-insensitive
                if(lineIgnoreCase.startsWith(ContentLengthHeader.NAME.toLowerCase())) { // naive Content-Length header parsing to figure out how much bytes of message body must be read after the SIP headers
                    contentLength = Integer.parseInt(line.substring(
                            ContentLengthHeader.NAME.length()+1).trim());
                } else if(lineIgnoreCase.startsWith(CallIdHeader.NAME.toLowerCase())) { // naive Content-Length header parsing to figure out how much bytes of message body must be read after the SIP headers
                    callId = line.substring(
                            CallIdHeader.NAME.length()+1).trim();
                }
            } else {
                if(isPreviousLineCRLF) {
                    crlfReceived = false;
                } else {
                    crlfReceived = true;
                }

                if(message.length() > 0) { // if we havent read any headers yet we are between messages and ignore CRLFs
                    readingMessageBodyContents = true;
                    readingHeaderLines = false;
                    partialLineRead = false;
                    message.append(CRLF); // the parser needs CRLF at the end, otherwise fails TODO: Is that a bug?

                    contentReadSoFar = 0;
                    messageBody = new byte[contentLength];
                }
            }
        }
        return crlfReceived;
    }

    private int readSingleByte(ByteBuf inputStream) throws IOException {
        sizeCounter --;
        checkLimits();
        if(inputStream.readableBytes()==0)
            return -1;
        return inputStream.readByte();
    }

    private void checkLimits() {
        if(maxMessageSize > 0 && sizeCounter < 0) throw new RuntimeException("Max Message Size Exceeded " + maxMessageSize);
    }

    private String readLine(ByteBuf inputStream) throws IOException {
        partialLineRead = false;
        int counter = 0;
        int increment = 1024;
        int bufferSize = increment;
        byte[] lineBuffer = new byte[bufferSize];
        // handles RFC 5626 CRLF keepalive mechanism
        byte[] crlfBuffer = new byte[2];
        int crlfCounter = 0;
        while (true) {
            char ch;
            int i = readSingleByte(inputStream);
            if (i == -1) {
                partialLineRead = true;
                currentStreamEnded = true;
                break;
            } else
                ch = (char) ( i & 0xFF);

            if (ch != '\r')
                lineBuffer[counter++] = (byte) (i&0xFF);
            else if (counter == 0)
                crlfBuffer[crlfCounter++] = (byte) '\r';

            if (ch == '\n') {
                if(counter == 1 && crlfCounter > 0) {
                    crlfBuffer[crlfCounter++] = (byte) '\n';
                }
                break;
            }

            if( counter == bufferSize ) {
                byte[] tempBuffer = new byte[bufferSize + increment];
                System.arraycopy((Object)lineBuffer,0, (Object)tempBuffer, 0, bufferSize);
                bufferSize = bufferSize + increment;
                lineBuffer = tempBuffer;
            }
        }
        if(counter == 1 && crlfCounter > 0) {
            return new String(crlfBuffer,0,crlfCounter,"UTF-8");
        } else {
            return new String(lineBuffer,0,counter,"UTF-8");
        }
    }


    private int readChunk(ByteBuf inputStream, byte[] where, int offset, int length) throws IOException {
        int read=0;

        if(inputStream.readableBytes() >= length){
            inputStream.readBytes(where, offset, length);
            read=length;
        }
        else{
            preservedBuff=inputStream.copy();
            read=-1;
        }

        sizeCounter -= read;
        checkLimits();
        return read;
    }

    private void readMessageBody(ByteBuf inputStream) throws IOException {
        int bytesRead = 0;

        if(contentLength>0) {
            bytesRead = readChunk(inputStream, messageBody, contentReadSoFar, contentLength-contentReadSoFar);
            if(bytesRead == -1) {
                currentStreamEnded = true;
                bytesRead = 0; // avoid passing by a -1 for a one-off bug when contentReadSoFar gets wrong
            }
        }
        contentReadSoFar += bytesRead;
        if(contentReadSoFar == contentLength) { // We have read the full message headers + body
            sizeCounter = maxMessageSize;
            readingHeaderLines = true;
            readingMessageBodyContents = false;
            final String msgLines = message.toString();
            message = new StringBuffer();
            final byte[] msgBodyBytes = messageBody;
            final int finalContentLength = contentLength;

            synchronized(smp) {

//                    sipMessage = smp.parseSIPMessage(msgLines.getBytes(), false, false, null);
//                    sipMessage.setMessageContent(msgBodyBytes);
//                    logger.info(sipMessage.toString());


                    this.ctx.fireChannelRead(new String(msgLines) + new String(msgBodyBytes));
                }

            this.contentLength = 0;
//            processSIPMessage(sipMessage);
        }

    }


}
