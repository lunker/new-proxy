package org.lunker.new_proxy.sip.wrapper.message;

import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.*;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.lunker.new_proxy.core.ConnectionManager;
import org.lunker.new_proxy.sip.util.SipMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.*;
import javax.sip.message.Request;
import java.net.InetSocketAddress;
import java.text.ParseException;

/**
 *
 * jain sip message wrapper for general purpose
 * Base class for LB, Proxy SipMessage
 * Created by dongqlee on 2018. 4. 26..
 */
public class DefaultSipMessage {
    private Logger logger= LoggerFactory.getLogger(DefaultSipMessage.class);

    protected SipMessageFactory sipMessageFactory;
    protected SIPMessage message;
    protected String method;
    protected ConnectionManager connectionManager=ConnectionManager.getInstance();

    public static DefaultSipMessage DEFUALT_MESSAGE=new DefaultSipMessage();

    public DefaultSipMessage(){

    }

    public DefaultSipMessage(SIPMessage sipMessage) {
        this.message=sipMessage;
        this.sipMessageFactory=SipMessageFactory.getInstance();
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

    public ViaList getViaHeaders(){
        return this.message.getViaHeaders();
    }

    public Via getTopmostVia(){
        return this.message.getTopmostVia();
    }

    public Authorization getAuthorization(){
        return this.message.getAuthorization();
    }

    public SIPMessage getRawSipMessage(){
        return this.message;
    }

    public void send() throws ParseException {
        String remoteHost="";
        int remotePort=0;
        String remoteTransport="";
        ChannelHandlerContext targetCtx=null;

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
            remoteHost=requestUri.getHost();
            remotePort=requestUri.getPort();

            remoteTransport=requestUri.getTransportParam();
        }
        else{
            // Response
            SIPResponse sipResponse=(SIPResponse) this.message;

            Via topVia=sipResponse.getTopmostVia();
            remoteHost=topVia.getReceived();
            remotePort=topVia.getRPort();

            remoteTransport=topVia.getTransport().toLowerCase();
        }

        // TODO: refactoring
        targetCtx=this.connectionManager.getClientConnection(remoteHost, remotePort, remoteTransport);

        if(targetCtx!=null){
            if("tcp".equals(remoteTransport)){
                // tcp
                ChannelFuture cf=targetCtx.writeAndFlush((Unpooled.copiedBuffer(this.message.toString(), CharsetUtil.UTF_8)));
                targetCtx.flush();
            }
            else if("udp".equals(remoteTransport)){
                // udp
                targetCtx.writeAndFlush(new DatagramPacket(
                        Unpooled.copiedBuffer(this.message.toString(), CharsetUtil.UTF_8),
                        new InetSocketAddress(remoteHost, remotePort)));
            }

            logger.info(String.format("[Success][%s] Send message\n%s\n", String.format("%s:%d", remoteHost, remotePort), this.message));
        }
        else {
            logger.info(String.format("[Fail][%s] Send message\n%s\nfailed cause : %s", String.format("%s:%d", remoteHost, remotePort), this.message, "targetCtx is null"));
        }
    }

    public MaxForwardsHeader getMaxForwards(){
        return this.message.getMaxForwards();
    }

    public CSeqHeader getCSeq(){
        return this.message.getCSeq();
    }

    public RouteList getRouteHeaders(){
        return this.message.getRouteHeaders();
    }

    public RecordRouteList getRecordRouteHeaders(){
        return this.message.getRecordRouteHeaders();
    }

    @Override
    public String toString() {
        return this.message.toString();
    }

    public Object clone() {
        SIPMessage clonedSipMessage=(SIPMessage) this.message.clone();

        return new DefaultSipMessage(clonedSipMessage);
    }

}
