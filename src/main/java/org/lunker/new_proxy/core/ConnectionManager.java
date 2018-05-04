package org.lunker.new_proxy.core;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dongqlee on 2018. 5. 5..
 */
public class ConnectionManager {
    private Logger logger= LoggerFactory.getLogger(ConnectionManager.class);
    private static ConnectionManager instance=null;
    private Map<String, ChannelHandlerContext> clientMap=null;

    private ConnectionManager() {
        this.clientMap=new ConcurrentHashMap<>();
    }

    public static ConnectionManager getInstance() {
        if(instance==null)
            instance=new ConnectionManager();
        return instance;
    }

    public void addClient(String host, int port, String transport, ChannelHandlerContext channelHandlerContext){
        String key=createClientKey(host, port, transport);

        this.clientMap.put(key, channelHandlerContext);

        if(logger.isDebugEnabled())
            logger.debug("Add Client :: " + key);
    }

    /*
    public Optional<ChannelHandlerContext> getClientConnection(String host, int port, String transport){
        String key=createClientKey(host, port, transport);
        Optional<ChannelHandlerContext> optionalChannelHandlerContext;
        return optionalChannelHandlerContext=Optional.ofNullable(this.clientMap.get(key));
    }
    */

    public ChannelHandlerContext getClientConnection(String host, int port, String transport){
        String key=createClientKey(host, port, transport);

        return this.clientMap.get(key);
    }

    public void deleteClient(String host, int port, String transport){
        String key=createClientKey(host, port, transport);

        if(this.clientMap.containsKey(key)){
            logger.info("Current Clients:: {}", this.clientMap.size());

            this.clientMap.remove(key);
            if(logger.isDebugEnabled())
                logger.debug("Delete Client success :: {}", key);
        }
        else{
            if(logger.isDebugEnabled())
                logger.info("Delete Client fail :: {}", key);
        }

    }

    private String createClientKey(String host, int port, String transport){
        return String.format("%s:%d:%s", host, port, transport);
    }
}
