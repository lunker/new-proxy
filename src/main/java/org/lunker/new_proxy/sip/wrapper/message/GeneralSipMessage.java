package org.lunker.new_proxy.sip.wrapper.message;

import gov.nist.javax.sip.message.SIPMessage;
import org.lunker.new_proxy.sip.context.ProxyContext;
import org.lunker.new_proxy.sip.session.ss.SipSessionKey;
import org.lunker.new_proxy.sip.util.SipMessageFactory;
import org.lunker.new_proxy.stub.session.sas.SipApplicationSession;
import org.lunker.new_proxy.stub.session.ss.SipSession;

import javax.sip.header.*;
import javax.sip.message.Request;
import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public abstract class GeneralSipMessage {
    protected SIPMessage message;
    protected SipSessionKey sipSessionKey;
    protected SipMessageFactory sipMessageFactory;
    protected Map<String, Object> attributes;
    protected ProxyContext proxyContext;
    protected String method;

    protected GeneralSipMessage() {
    }

    protected GeneralSipMessage(SIPMessage message, SipSessionKey sipSessionKey) {
        this.message = message;
        this.sipSessionKey = sipSessionKey;
        this.proxyContext=ProxyContext.getInstance();
        this.sipMessageFactory=SipMessageFactory.getInstance();
    }

    public SipSessionKey getSipSessionKey() {
        return sipSessionKey;
    }

    public String getMethod(){
        if (this.method == null) {
            this.method = this.message instanceof Request ? ((Request)this.message).getMethod() : ((CSeqHeader)this.message.getHeader("CSeq")).getMethod();
        }

        return this.method;
    }

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

    public String getCallId(){
        CallIdHeader id = (CallIdHeader)this.message.getHeader("Call-ID");
        return id != null ? id.getCallId() : null;
    }

    /*
    public String getHeader(String headerName){
        String value = null;
        if (this.message.getHeader(headerName) != null) {
            value = ((SIPHeader)this.message.getHeader(headerName)).getValue();
        }

        return value;
    }
    */

    public void addHeader(Header header){
        this.message.addHeader(header);
    }

    public Header getHeader(String headerName) throws NullPointerException{
        if(this.message==null)
            throw new NullPointerException("");

        return this.message.getHeader(headerName);
    }

    public SipSession getSipSession(){
        return this.proxyContext.getSipSession(sipSessionKey);
    }

    public SipApplicationSession getSipApplicationSession(){
        return this.proxyContext.getSipApplicationSession(sipSessionKey);
    }

    public SIPMessage getRawSipMessage(){
        return this.message;
    }

    public void send(){
        // 연결된 LB가 없으면, SIP Message에 있는 정보로 전송한다


        if(this.getSipSession()!=null){
            if(this instanceof GeneralSipRequest){
                // send to other session's ctx


            }
            else{
                // send to this session's ctx
                this.getSipSession().getCtx().writeAndFlush(this.message);
            }
        }
    }

    @Override
    public String toString() {
        return this.message.toString();
    }
}
