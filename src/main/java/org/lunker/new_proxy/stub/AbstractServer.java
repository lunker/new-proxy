package org.lunker.new_proxy.stub;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.server.TransportInitializer;

import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public abstract class AbstractServer extends ChannelInboundHandlerAdapter{

    protected TransportInitializer channelInitializer=null;
    protected Map<String, Object> transportConfigMap=null;

    abstract public ChannelFuture run() throws Exception;
}
