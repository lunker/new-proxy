package org.lunker.new_proxy.sip.wrapper.message.proxy;

import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import org.lunker.new_proxy.core.ProxyContext;
import org.lunker.new_proxy.sip.session.ss.SipSessionKey;
import org.lunker.new_proxy.sip.util.SipMessageFactory;
import org.lunker.new_proxy.sip.wrapper.message.AbstractSipMessage;
import org.lunker.new_proxy.stub.session.sas.SipApplicationSession;
import org.lunker.new_proxy.stub.session.ss.SipSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.*;
import javax.sip.message.Request;
import java.text.ParseException;
import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public abstract class ProxySipMessage extends AbstractSipMessage{

    protected Logger logger= LoggerFactory.getLogger(ProxySipMessage.class);
    protected SIPMessage message;
    protected SipSessionKey sipSessionKey;
    protected SipMessageFactory sipMessageFactory;
    protected Map<String, Object> attributes;
    protected ProxyContext proxyContext;
    protected String method;

    //TODO: create pre-defined static messages
//    public static ProxySipMessage SERVER_INTERNAL_ERROR_500=new ProxySipResponse(new SIPResponse(), new SipSessionKey());

    protected ProxySipMessage() {
    }

    protected ProxySipMessage(SIPMessage message, SipSessionKey sipSessionKey) {
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

        // TODO:: PostProcessor로 옮긴다.
        /**
         * Request:
         *
         * // Client -> proxy (Direct Connection)
         *  1) - request uri 정보를 읽어와서 client connection을 뒤지고, 해당 socket에 전송한다
         *
         * // Client -> LB -> Proxy
         *  2) - request uri의 정보를 읽어와서 client connection을 못찾으면,
         *  top via의 정보를 읽어서 해당 노드의 정보가 LB인지 확인한다.
         *  LB와 일치하면, 해당 LB에게 전송한다.
         *
         * Response:
         *  -> Via를 뒤져서, 해당 connection을 직접 가지고 있으면 전송, 아니면 lb에게 전송
         *  -> Via를 뒤져서,
         */
        if(this.message instanceof SIPRequest){
            // Request
            SIPRequest sipRequest=(SIPRequest) this.message;
            SipUri requestUri=(SipUri) sipRequest.getRequestURI();
            String remoteHost=requestUri.getHost();
            int remotePort=requestUri.getPort();


            System.out.println("break");
        }
        else{
            // Response
            SIPResponse sipResponse=(SIPResponse) this.message;

            sipResponse.getTopmostVia();
        }


        /*
        if(this.getSipSession()!=null){
            if(this instanceof ProxySipRequest){
                // send to other session's ctx
                String toAor="";
                ChannelHandlerContext targetCtx=null;

                Registration targetRegistration=null; // .....? 이건 Proxy에서 Set어쩌고를 통해서 다 해줄거다. 나는 그냥

                toAor=this.message.getToHeader().getAddress().getURI().toString().split(":")[1];
                targetRegistration=proxyContext.getRegistrar().getRegistration(toAor);
                targetCtx=proxyContext.getRegistrar().getCtx(toAor);

                Request targetRequest=(Request) ((ProxySipRequest) this).message;
                SipUri requestUri = new SipUri();

                requestUri.setHost(targetRegistration.getRemoteAddress());
                requestUri.setPort(targetRegistration.getRemotePort());

                targetRequest.setRequestURI(((ProxySipRequest) this).message.getTo().getAddress().getURI());

                ChannelFuture cf=targetCtx.writeAndFlush((Unpooled.copiedBuffer(((ProxySipRequest) this).message.toString(), CharsetUtil.UTF_8)));
                targetCtx.flush();

                if (!cf.isSuccess()) {
                    logger.warn("Send failed: " + cf.cause());
                }

                logger.info("[SENT]:\n" + ((ProxySipRequest) this).message.toString());
            }
            else{
                // send to this session's ctx
                ChannelHandlerContext targetCtx=this.getSipSession().getCtx();
                ChannelFuture cf=targetCtx.writeAndFlush(Unpooled.copiedBuffer(this.message.toString(), CharsetUtil.UTF_8));

                targetCtx.flush();
                if (!cf.isSuccess()) {
                    logger.warn("Send failed: " + cf.cause());
                }

                logger.info("[SENT]:\n" + ((ProxySipResponse) this).message.toString());
            }
        }// end-if
        */


    }

    @Override
    public String toString() {
        return this.message.toString();
    }
}
