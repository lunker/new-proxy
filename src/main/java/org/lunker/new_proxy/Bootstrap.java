package org.lunker.new_proxy;

import io.netty.channel.ChannelFuture;
import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.core.constants.ServerType;
import org.lunker.new_proxy.exception.BootstrapException;
import org.lunker.new_proxy.exception.InvalidConfigurationException;
import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.sip.processor.lb.LoadBalancerPreProcessor;
import org.lunker.new_proxy.sip.processor.proxy.ProxyPreProcessor;
import org.lunker.new_proxy.stub.AbstractServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public class Bootstrap {
    private static Logger logger=LoggerFactory.getLogger(Bootstrap.class);
    private static Configuration configuration=Configuration.getInstance();
    private static List<Mono<ChannelFuture>> serverList=new ArrayList<>();

    public static void addHandler(Transport transport, Class sipMessageHandlerImplClass) throws BootstrapException {
        addHandler(transport, sipMessageHandlerImplClass.getName());
    }

    // ISSUE:tcp, udp 등 여러 서버들간에 동일한 Handler 객체를 넘겨줘도 되는가? 아니면 각각 서버들마다 다른 객체를 넘겨줘야하나?
    public static void addHandler(Transport transport, String sipMessageHandlerImplClassName) throws BootstrapException {
        try{
            Mono<ChannelFuture> serverThread=null;
            ServerInfo serverInfo=null;
            ServerProcessor serverProcessor=null;

            serverInfo=generateServerInfo(configuration.getServerType(), (String) configuration.getConfigMap(transport).get("host"), (int) configuration.getConfigMap(transport).get("port"), transport);
            serverProcessor=generateServerProcessor(serverInfo, sipMessageHandlerImplClassName);
            serverThread=generateServerThread(serverInfo, serverProcessor);

            serverList.add(serverThread);
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error("[{}] Server started failed", transport);
            throw new BootstrapException(e.getMessage());
        }
    }

    // TODO:
    public static void addShutdownHandler(){
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("Shutdown hook ran!");
            }
        });
    }

    public static void run() throws Exception{
        Mono<ChannelFuture> serverMono=null;
        ChannelFuture result=null;

        for(int idx=0; idx<serverList.size(); idx++){
            final int cnt=idx;
            serverMono=serverList.get(idx);

            serverMono.subscribe((channelFuture)->{
                if(cnt==serverList.size()-1){
                    try{
                        channelFuture.channel().closeFuture().await();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private static ServerInfo generateServerInfo(ServerType serverType, String host, int port, Transport transport){
        return new ServerInfo(serverType, host, port, transport);
    }

    private static ServerProcessor generateServerProcessor(ServerInfo serverInfo, String sipMessageHandlerClassName) throws InvalidConfigurationException,ClassNotFoundException, IllegalAccessException, InstantiationException {
        ServerProcessor serverProcessor=new ServerProcessor();

        switch (serverInfo.getServerType()){
            case LB:
                // TODO: Refactoring
                serverProcessor.setServerInfo(serverInfo);
                serverProcessor.setSipMessageHandlerClassName(sipMessageHandlerClassName);
                serverProcessor.setPreProcessor(new LoadBalancerPreProcessor(serverProcessor.getSipMessageHandler()));



                break;
            case PROXY:
                serverProcessor.setServerInfo(serverInfo);
                serverProcessor.setSipMessageHandlerClassName(sipMessageHandlerClassName);
                serverProcessor.setPreProcessor(new ProxyPreProcessor(serverProcessor.getSipMessageHandler()));

                // TODO: postProcessor에서 공통로직 후처리
//                serverProcessor.setPostProcessor(new ProxyPostProcessor());
                break;
            case NONE:
                throw new InvalidConfigurationException("ServerType is not valid");
        }

        return serverProcessor;
    }

    // TODO: change mono->
    private static Mono<ChannelFuture> generateServerThread(ServerInfo serverInfo, ServerProcessor serverProcessor){
        Mono<ChannelFuture> serverThread=Mono.fromCallable(()->{
            AbstractServer server=null;
            ChannelFuture f=null;

            if(logger.isDebugEnabled())
                logger.debug("[{}] Server starting ...", serverInfo.getTransport().getValue());

            server=AbstractServer.create(serverInfo, serverProcessor, configuration.getConfigMap(serverInfo.getTransport()));

            try{
                f=server.run();
                if(logger.isDebugEnabled())
                    logger.debug("[{}] Server started", serverInfo.getTransport().getValue());
            }
            catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(String.format("[%s] Server starting error. cause, %s", serverInfo.getTransport(), e.getMessage()));
            }

            return f;
        });

        serverThread.subscribeOn(Schedulers.newElastic("elelel"));
        return serverThread;
    }


}
