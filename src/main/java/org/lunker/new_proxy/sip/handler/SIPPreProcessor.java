package org.lunker.new_proxy.sip.handler;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.sip.context.ProxyContext;
import org.lunker.new_proxy.sip.util.SipMessageFactory;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipRequest;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipResponse;
import org.lunker.new_proxy.stub.session.ss.SipSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.text.ParseException;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class SIPPreProcessor extends ChannelInboundHandlerAdapter {

    private Logger logger= LoggerFactory.getLogger(SIPPreProcessor.class);
    private StringMsgParser stringMsgParser=null;
    private SipMessageFactory sipMessageFactory=null;
    private ProxyContext proxyContext=ProxyContext.getInstance();
    private SIPHandler sipHandler=null;

    public SIPPreProcessor() {
        this.stringMsgParser=new StringMsgParser();
        this.sipHandler=new SIPHandler();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // socket -> byte : nonblocking :: library :: netty

        // message :: reactive. Java nonblocking
                //->

        // Servlet
        // Message 1 -> Thread 1


        // Reactive
        //


        // b2bua
        // Mobicents, rfc
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            Mono<String> rawSipMessageMono=Mono.just((String) msg);


            rawSipMessageMono.map((message)->{

                try{
                    logger.info("Before deserialized");
                    return deserialize(ctx, message);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }).map((message)->{
                logger.info("Before process");
                return this.sipHandler.process(ctx, message);
            }).subscribe((messageList)->{

                messageList.stream().forEach((message)->{
                    GeneralSipMessage generalSipMessage=(GeneralSipMessage) message;

                    try{
                        generalSipMessage.send();
                        logger.info("Send !");
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                });


            });
        }
        catch (Exception e){
            logger.warn("Error while encoding sip wrapper . . . :\n" + ((String) msg));
            ctx.fireExceptionCaught(e);
        }
    }

    // Mobicents
    // SipServletRequest <- SIPRequest(Jain)
    // SipServletResponse <- SIPResponse(Jain)
    // SipServletMessage <- SIPMessage(Jain)
    public GeneralSipMessage deserialize(ChannelHandlerContext ctx, String message) throws ParseException{
        GeneralSipMessage generalSipMessage=null;

        SIPMessage jainSipMessage=stringMsgParser.parseSIPMessage(message.getBytes(), true, false, null);

        // TODO(lunker): message send를 위해 ctx를 session에 저장시킨다
        SipSession sipSession=proxyContext.createOrGetSIPSession(ctx, jainSipMessage);

        /*
        // update Via
        ViaList viaList=jainSipMessage.getViaHeaders();

        Via topViaHeader=(Via) viaList.getFirst();

        if (topViaHeader.getReceived() == null) {
            String received=((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
            topViaHeader.setReceived(received);
        }

        if(topViaHeader.getRPort() == 0 || topViaHeader.getRPort() == -1) {
            int rport=((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
            topViaHeader.setParameter("rport", rport+"");
        }

        viaList.set(0, topViaHeader);
        jainSipMessage.setHeader(viaList);
        */

        if(jainSipMessage instanceof SIPRequest){
            generalSipMessage=new GeneralSipRequest(jainSipMessage, sipSession.getSipSessionkey());
        }
        else{
            generalSipMessage=new GeneralSipResponse(jainSipMessage, sipSession.getSipSessionkey());
        }

        if(jainSipMessage instanceof SIPRequest && sipSession.getFirstRequest()==null && ((SIPRequest) jainSipMessage).getMethod().equals("INVITE")){
            sipSession.setFirstRequest((GeneralSipRequest) generalSipMessage);
        }

        return generalSipMessage;
    }
}
