package org.lunker.new_proxy;

import org.lunker.new_proxy.server.tcp.TCPServer;
import org.lunker.new_proxy.util.Closer;

/**
 * Created by dongqlee on 2018. 3. 15..
 */
public class Application {

    public static void main(String[] args) throws Exception {


        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                Closer.graceFullyShutdown();
            }
        });

        TCPServer tcpServer=new TCPServer();
        tcpServer.run();
    }

}
