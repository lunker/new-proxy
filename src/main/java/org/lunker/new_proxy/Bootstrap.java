package org.lunker.new_proxy;

import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.core.constants.ServerType;
import org.lunker.new_proxy.exception.InvalidConfiguratoinException;
import org.lunker.new_proxy.server.tcp.TCPServer;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.sip.processor.proxy.ProxyPostProcessor;
import org.lunker.new_proxy.sip.processor.proxy.ProxyPreProcessor;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public class Bootstrap {
    private static Logger logger=LoggerFactory.getLogger(Bootstrap.class);
    private static Configuration configuration=Configuration.getInstance();

    // ISSUE:
    // tcp, udp 등 여러 서버들간에 동일한 Handler 객체를 넘겨줘도 되는가? 아니면 각각 서버들마다 다른 객체를 넘겨줘야하나?
    public static void start(String transport, SipMessageHandler sipMessageHandler) throws InvalidConfiguratoinException{
        logger.debug(String.format("Server %s starting ...", transport));

        Optional<SipMessageHandler> optionalSipMessageHandler=Optional.ofNullable(sipMessageHandler);

        //TODO: using constants
        if(transport.equals("tcp")){
            if(configuration.isValidTCP()){
                ServerProcessor serverProcessor=generateProcessor(configuration.getServerType(), optionalSipMessageHandler);
                TCPServer tcpServer=new TCPServer(serverProcessor, configuration.getTcpConfigMap());

                try{
                    tcpServer.run();
                }
                catch (Exception e){
                    e.printStackTrace();
                    throw new RuntimeException("Server starting error.");
                }
            }
            else
                throw new RuntimeException("Server starting error. Configuration is not valid");
        }

        logger.debug(String.format("Server %s started", transport));
    }

    private static ServerProcessor generateProcessor(ServerType serverType, Optional<SipMessageHandler> sipMessageHandler) throws InvalidConfiguratoinException{
        ServerProcessor serverProcessor=new ServerProcessor();

        switch (serverType){
            case LB:
                //TODO: Create LB Processor
                break;
            case PROXY:
                serverProcessor.setSipMessageHandler(sipMessageHandler);
                serverProcessor.setPreProcessor(new ProxyPreProcessor(sipMessageHandler));
                serverProcessor.setPostProcessor(new ProxyPostProcessor());
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
