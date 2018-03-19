package org.lunker.new_proxy.sip;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class SIPMessageBuilder {

    public static final byte COLON = ':';

    public static final byte CR = '\r';

    public static final byte LF = '\n';

    public static final byte SP = ' ';

    public static final byte HTAB = '\t';


    /**
     * Every SIP message has an initial line, which is either a request line or
     * a response line. We don't care which but this is that one line...
     */
    private byte[] initialLine;
    private int initalLineIndex = 0;

    /**
     * All the headers from the SIP message will be stored in this buffer.
     */
    private byte[] headers;
    private int headersIndex = 0;

    /**
     * The payload of the SIP message.
     */
    private byte[] payload;
    private int payloadIndex = 0;

    private int contentLength = -1;


    public void write(byte b){



    }

}
