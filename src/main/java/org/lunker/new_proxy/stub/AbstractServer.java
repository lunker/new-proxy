package org.lunker.new_proxy.stub;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.server.TransportChannelInitializer;
import org.lunker.new_proxy.server.tcp.TcpChannelChannelInitializer;
import org.lunker.new_proxy.server.tcp.TcpServer;
import org.lunker.new_proxy.server.udp.UdpServer;
import org.lunker.new_proxy.server.websocket.WebsocketServer;
import org.lunker.new_proxy.sip.processor.ServerProcessor;

import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public abstract class AbstractServer extends ChannelInboundHandlerAdapter{

    protected TransportChannelInitializer channelInitializer=null;
    protected Map<String, Object> transportConfigMap=null;

    abstract public ChannelFuture run() throws Exception;

    public static AbstractServer create(Transport transport, TransportChannelInitializer transportChannelInitializer){
        AbstractServer server=null;

        // TODO: TransportChannelInitializer어디서 만들지~

        if(Transport.TCP.equals(transport)){
            server=new TcpServer(new TcpChannelChannelInitializer());
        }
        else if(Transport.UDP.equals(transport)){
            // TODO: configure UDP server
            server=new UdpServer(null);
        }
        else if(Transport.TLS.equals(transport)){
            // TODO: configure tls server
        }
        else if(Transport.WS.equals(transport)){
            // TODO: configure websocket server
            server=new WebsocketServer(false, );
        }
        else if(Transport.WSS.equals(transport)){
            // TODO: configure websocket server
            server=new WebsocketServer(true, );
        }

        return server;
    }
}
