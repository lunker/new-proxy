package org.lunker.new_proxy.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HeartbeatConnections {
    private Logger logger = LoggerFactory.getLogger(HeartbeatConnections.class);

    private Map<String, ChannelHandlerContext> connectionMap;

    private static class LazyInstanceHolder {
        private static final HeartbeatConnections instance = new HeartbeatConnections();
    }

    public static HeartbeatConnections getInstance() {
        return LazyInstanceHolder.instance;
    }

    private HeartbeatConnections() {
        // TODO: connection limit?
        connectionMap = new ConcurrentHashMap<>(1000);
    }

    public void add(String key, ChannelHandlerContext channelHandlerContext) {
        connectionMap.put(key, channelHandlerContext);
        logger.debug("added heartbeat connection: " + key);
    }

    public void add(String host, int port, ChannelHandlerContext channelHandlerContext) {
        this.add(generateKey(host, port), channelHandlerContext);
    }

    public ChannelHandlerContext get(String key) {
        return this.connectionMap.get(key);
    }

    public ChannelHandlerContext get(String host, int port) {
        return this.get(generateKey(host, port));
    }

    public void remove(String key) {
        this.connectionMap.remove(key);
        logger.debug("removed heartbeat connection: " + key);
    }

    public void remove(String host, int port) {
        this.remove(generateKey(host, port));
    }

    public String generateKey(String host, int port) {
        return String.format("%s:%d", host, port);
    }
}
