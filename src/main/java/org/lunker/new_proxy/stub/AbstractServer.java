package org.lunker.new_proxy.stub;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.server.TransportInitializer;
import org.lunker.new_proxy.server.tcp.TCPServer;
import org.lunker.new_proxy.server.udp.UDPServer;
import org.lunker.new_proxy.server.websocket.WebsocketServer;
import org.lunker.new_proxy.sip.processor.ServerProcessor;

import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public abstract class AbstractServer extends ChannelInboundHandlerAdapter{

    protected TransportInitializer channelInitializer=null;
    protected Map<String, Object> transportConfigMap=null;

    abstract public ChannelFuture run() throws Exception;

    //TODO: Server Factory
    public static AbstractServer create(ServerInfo serverInfo, ServerProcessor serverProcessor, Map<String, Object> transportConfigMap){
        AbstractServer server=null;

        //TODO: using constants
        if(Transport.TCP.equals(serverInfo.getTransport())){
            server=new TCPServer(serverProcessor, transportConfigMap);
        }
        else if(Transport.UDP.equals(serverInfo.getTransport())){
            // TODO: configure UDP server
            server=new UDPServer(serverProcessor, transportConfigMap);
        }
        else if(Transport.TLS.equals(serverInfo.getTransport())){
            // TODO: configure tls server
        }
        else if(Transport.WS.equals(serverInfo.getTransport())){
            // TODO: configure websocket server
            server=new WebsocketServer(serverProcessor, transportConfigMap);
        }
        else if(Transport.WSS.equals(serverInfo.getTransport())){
            // TODO: configure websocket server
        }

        return server;
    }
}
