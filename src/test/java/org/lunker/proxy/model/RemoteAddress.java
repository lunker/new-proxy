package org.lunker.proxy.model;

/**
 * Created by dongqlee on 2018. 5. 18..
 */
public class RemoteAddress {
    private String host;
    private int port;

    public RemoteAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
