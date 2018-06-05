package org.lunker.new_proxy.server.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.server.TransportChannelInitializer;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.sip.processor.SipPreProcessor;
import org.lunker.new_proxy.sip.processor.proxy.ProxyPreProcessor;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class TcpChannelChannelInitializer extends TransportChannelInitializer {
    private Logger logger= LoggerFactory.getLogger(TcpChannelChannelInitializer.class);

    public TcpChannelChannelInitializer() {
        //
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        // byte -> str
        ch.pipeline().addLast("decoder", new TcpStreamDecoder());


        // PreProcessor도 생성해서 받아옴
        // str -> lb or proxy message
        // PreProcessor는 ServerInfo를 알아야함. .  . .
        ch.pipeline().addLast("preProcessor", new SipPreProcessor()); // Lb, Proxy


        //org.lunker.proxy.sip.SipServletImpl.class;
        // SipMessageHandlerImpl 객체는 받아오고
        ch.pipeline().addLast("asdf", SipmessageHandlerImpl);

        tcpManager.send(sipMessage)' '
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }


    public static TcpChannelChannelInitializer create(SipMessageHandler sipMessageHandler){
        return new

    }
}
