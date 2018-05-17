package org.lunker.new_proxy;

import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.core.constants.ServerType;
import org.lunker.new_proxy.exception.InvalidConfiguratoinException;
import org.lunker.new_proxy.exception.ServerStartException;
import org.lunker.new_proxy.server.tcp.TCPServer;
import org.lunker.new_proxy.server.udp.UDPServer;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public class Bootstrap {
    private static Logger logger=LoggerFactory.getLogger(Bootstrap.class);
    private static Configuration configuration=Configuration.getInstance();

    public static void start(String transport, Class sipMessageHandlerImplClass) throws ServerStartException {
        start(transport, sipMessageHandlerImplClass.getName());
    }

    // ISSUE:tcp, udp 등 여러 서버들간에 동일한 Handler 객체를 넘겨줘도 되는가? 아니면 각각 서버들마다 다른 객체를 넘겨줘야하나?
    public static void start(String transport, String sipMessageHandlerImplClassName) throws ServerStartException {
        if(logger.isDebugEnabled())
            logger.debug("[{}] Server starting ...", transport);

        try{
            ServerProcessor serverProcessor=generateServerProcessor(configuration.getServerType(), sipMessageHandlerImplClassName);

            //TODO: using constants
            if("tcp".equalsIgnoreCase(transport)){
                if(configuration.isValidTCP()){
                    TCPServer tcpServer=new TCPServer(serverProcessor, configuration.getTcpConfigMap());

                    try{
                        tcpServer.run();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        throw new RuntimeException(String.format("[%s] Server starting error.", transport));
                    }
                }
                else
                    throw new RuntimeException(String.format("[%s] Server starting error. Configuration is not valid", transport));
            }
            else if("udp".equalsIgnoreCase(transport)){
                // TODO: configure UDP server
                if (configuration.isValidUDP()) {
                    UDPServer udpServer = new UDPServer(serverProcessor, configuration.getUdpConfigMap());

                    try {
                        udpServer.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(String.format("[%s] Server starting error.", transport));
                    }
                } else
                    throw new RuntimeException(String.format("[%s] Server starting error. Configuration is not valid", transport));
            }
            else if("tls".equalsIgnoreCase(transport)){
                // TODO: configure tls server
            }
            else if("ws".equalsIgnoreCase(transport)){
                // TODO: configure websocket server
            }

            if(logger.isDebugEnabled())
                logger.debug("[{}] Server started", transport);
        }
        catch (Exception e){
            logger.error("[{}] Server started failed", transport);
            throw new ServerStartException(e.getMessage());
        }

    }

    private static ServerProcessor generateServerProcessor(ServerType serverType, String sipMessageHandlerClassName) throws InvalidConfiguratoinException,ClassNotFoundException, IllegalAccessException, InstantiationException {
        ServerProcessor serverProcessor=new ServerProcessor();

        switch (serverType){
            case LB:
                //TODO: Create LB Processor
                break;
            case PROXY:
                serverProcessor.setSipMessageHandlerClassName(sipMessageHandlerClassName);

                // TODO: postProcessor에서 공통로직 후처리
//                serverProcessor.setPostProcessor(new ProxyPostProcessor());

                break;
            case NONE:
                throw new InvalidConfiguratoinException("ServerType is not valid");
        }

        return serverProcessor;
    }

    public static void startTCP(SipMessageHandler sipMessageHandler){
//        Assert.that(sipMessageHandler==null, "Sip Handler is not implemented");
//        assert sipMessageHandler ==null ?
    }

    public static void startUDP(){

    }

    public static void startAll(){

    }


}
