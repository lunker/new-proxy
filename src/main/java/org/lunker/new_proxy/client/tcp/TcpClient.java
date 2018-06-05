package org.lunker.new_proxy.client.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.exception.InvalidConfigurationException;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.sip.processor.lb.LoadBalancerPreProcessor;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class TcpClient {
    private Logger logger = LoggerFactory.getLogger(TcpClient.class);

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    Configuration configuration = Configuration.getInstance();
    Map<String, Object> transportConfigMap = configuration.getConfigMap(Transport.TCP);
    SipMessageHandler sipMessageHandler;
    PreProcessor preProcessor;

    public TcpClient(Class SipMessageHandlerImpl) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, InvalidConfigurationException {
//        this.transportConfigMap = transportConfigMap;
        this.sipMessageHandler = (SipMessageHandler) Class.forName(SipMessageHandlerImpl.getName()).getConstructor().newInstance();
        switch (configuration.getServerType()) {
            case LB:
                this.preProcessor = new LoadBalancerPreProcessor(this.sipMessageHandler);
                break;
            case PROXY:
                this.preProcessor = new LoadBalancerPreProcessor(this.sipMessageHandler);
                break;
            case NONE:
                throw new InvalidConfigurationException("Server Type is not valid");
        }
    }

    public ChannelFuture connect(String host, int port) throws Exception {
        Bootstrap bootstrap = new Bootstrap();

        Map<String, Object> tcpOptions=(Map<String, Object>)transportConfigMap.get("options");

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new TcpClientInitializer(preProcessor))
                .option(ChannelOption.SO_BACKLOG, (int) tcpOptions.get("so_backlog"))
                .option(ChannelOption.SO_LINGER, (int) tcpOptions.get("so_linger"))
                .option(ChannelOption.TCP_NODELAY, (boolean) tcpOptions.get("tcp_nodelay"))
                .option(ChannelOption.SO_REUSEADDR, (boolean) tcpOptions.get("so_reuseaddr"))
                .option(ChannelOption.SO_RCVBUF, (int) tcpOptions.get("so_rcvbuf"))
                .option(ChannelOption.SO_SNDBUF, (int) tcpOptions.get("so_sndbuf"));

        ChannelFuture channelFuture = bootstrap.connect(host, port);
        logger.info("connect to {}:{} using TCP", host, port);
        return channelFuture;
    }
}
