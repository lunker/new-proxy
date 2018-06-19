package org.lunker.new_proxy.model;

/**
 * Created by dongqlee on 2018. 5. 20..
 */
public enum Transport {
    NONE("none", -1),
    TCP("tcp", 1),
    TLS("tls", 2),
    UDP("udp", 0),
    WS("ws", 3),
    WSS("wss", 4);

    private String value;
    private int index;

    Transport(String value, int index) {
        this.value = value;
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public int getIndex() { return index; }

    @Override
    public String toString() {
        return "Transport{" +
                "value='" + value + '\'' +
                '}';
    }
}
