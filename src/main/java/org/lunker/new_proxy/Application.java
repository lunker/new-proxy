package org.lunker.new_proxy;

import org.lunker.new_proxy.server.tcp.TCPServer;
import org.lunker.new_proxy.server.websocket.WebsocketServer;
import org.lunker.new_proxy.util.Closer;

/**
 * Created by dongqlee on 2018. 3. 15..
 */
public class Application {

    private static TCPServer tcpServer=null;
    private static WebsocketServer websocketServer=null;


    public static void main(String[] args) throws Exception {

        tcpServer=new TCPServer();
        websocketServer=new WebsocketServer();

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
//                websocketServer.shutdown();
                tcpServer.shutdown();
                Closer.graceFullyShutdown();
            }
        });


        tcpServer.run();
//        websocketServer.run();
    }

}
