package org.lunker.new_proxy.sip.wrapper.message;

import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.message.SIPMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import org.lunker.new_proxy.sip.context.ProxyContext;
import org.lunker.new_proxy.sip.session.ss.SipSessionKey;
import org.lunker.new_proxy.sip.util.SipMessageFactory;
import org.lunker.new_proxy.stub.session.sas.SipApplicationSession;
import org.lunker.new_proxy.stub.session.ss.SipSession;
import org.lunker.new_proxy.util.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.*;
import javax.sip.message.Request;
import java.text.ParseException;
import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public abstract class GeneralSipMessage {

    protected Logger logger= LoggerFactory.getLogger(GeneralSipMessage.class);
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

    /**
     * Get SipSession
     * @return {@link SipSession}
     */
    public SipSession getSipSession(){
        return this.proxyContext.getSipSession(sipSessionKey);
    }

    public SipApplicationSession getSipApplicationSession(){
        return this.proxyContext.getSipApplicationSession(sipSessionKey);
    }

    public SIPMessage getRawSipMessage(){
        return this.message;
    }

    public void send() throws ParseException{
        // 연결된 LB가 없으면, SIPMessage에 있는 정보로 전송한다

        if(this.getSipSession()!=null){
            if(this instanceof GeneralSipRequest){
                // send to other session's ctx
                String toAor="";
                ChannelHandlerContext targetCtx=null;
                Registration targetRegistration=null;

                toAor=this.message.getToHeader().getAddress().getURI().toString().split(":")[1];
                targetRegistration=proxyContext.getRegistrar().getRegistration(toAor);
                targetCtx=proxyContext.getRegistrar().getCtx(toAor);

                Request targetRequest=(Request) ((GeneralSipRequest) this).message;
                SipUri requestUri = new SipUri();

                requestUri.setHost(targetRegistration.getRemoteAddress());
                requestUri.setPort(targetRegistration.getRemotePort());

                targetRequest.setRequestURI(((GeneralSipRequest) this).message.getTo().getAddress().getURI());

                ChannelFuture cf=targetCtx.writeAndFlush((Unpooled.copiedBuffer(((GeneralSipRequest) this).message.toString(), CharsetUtil.UTF_8)));
                targetCtx.flush();

                if (!cf.isSuccess()) {
                    logger.warn("Send failed: " + cf.cause());
                }

                logger.info("[SENT]:\n" + ((GeneralSipRequest) this).message.toString());
            }
            else{
                // send to this session's ctx
                ChannelHandlerContext targetCtx=this.getSipSession().getCtx();
                ChannelFuture cf=targetCtx.writeAndFlush(Unpooled.copiedBuffer(this.message.toString(), CharsetUtil.UTF_8));

                targetCtx.flush();
                if (!cf.isSuccess()) {
                    logger.warn("Send failed: " + cf.cause());
                }

                logger.info("[SENT]:\n" + ((GeneralSipResponse) this).message.toString());
            }
        }
    }

    @Override
    public String toString() {
        return this.message.toString();
    }
}
