package org.lunker.new_proxy.sip.wrapper.message;

import gov.nist.javax.sip.message.SIPMessage;
import org.lunker.new_proxy.sip.util.SipMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.*;
import javax.sip.message.Request;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public abstract class AbstractSipMessage {
    private Logger logger= LoggerFactory.getLogger(AbstractSipMessage.class);

    protected SipMessageFactory sipMessageFactory;
    protected SIPMessage message;
    protected String method;


    public String getMethod(){
        if (this.method == null) {
            this.method = this.message instanceof Request ? ((Request)this.message).getMethod() : ((CSeqHeader)this.message.getHeader("CSeq")).getMethod();
        }

        return this.method;
    }

    /**
     * Get 'From' Header
     * @return
     * @throws NullPointerException
     */
    public FromHeader getFrom() throws NullPointerException{
        if(this.message==null)
            throw new NullPointerException("");
        return this.message.getFrom();
    }

    public ToHeader getTo() throws NullPointerException{
        if(this.message==null)
            throw new NullPointerException("");
        return this.message.getTo();
    }

    public String getCallId() {
        CallIdHeader id = (CallIdHeader)this.message.getHeader("Call-ID");
        return id != null ? id.getCallId() : null;
    }

    public void addHeader(Header header){
        this.message.addHeader(header);
    }

    public Header getHeader(String headerName) throws NullPointerException{
        if(this.message==null)
            throw new NullPointerException("");

        return this.message.getHeader(headerName);
    }

    public void addHeader(AbstractSipMessage sipMessage) {

    }

    public void getHeader() {

    }
}
