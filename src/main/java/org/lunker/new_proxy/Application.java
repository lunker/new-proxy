package org.lunker.new_proxy;

import org.lunker.new_proxy.server.TCPServer;

/**
 * Created by dongqlee on 2018. 3. 15..
 */
public class Application {

    public static void main(String[] args) throws Exception {

        TCPServer tcpServer=new TCPServer();
        tcpServer.run();
    }

}
