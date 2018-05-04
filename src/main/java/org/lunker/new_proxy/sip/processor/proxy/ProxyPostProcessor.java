package org.lunker.new_proxy.sip.processor.proxy;

import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.sip.processor.PostProcessor;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public class ProxyPostProcessor extends PostProcessor {

    private Logger logger= LoggerFactory.getLogger(ProxyPostProcessor.class);

    public ProxyPostProcessor() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * - Send SipMessage
     *
     * (Stateful)
     * - Destroy session
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("POST-PROCESSOR");

        if(msg!=null){
            Optional<DefaultSipMessage> maybeProxySipMessage=(Optional<DefaultSipMessage>) msg;

            maybeProxySipMessage.ifPresent((defaultSipMessage)->{

                // Send ProxyMessage
                try{
                    defaultSipMessage.send();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
