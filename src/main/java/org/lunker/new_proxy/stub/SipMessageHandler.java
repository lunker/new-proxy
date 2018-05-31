package org.lunker.new_proxy.stub;

import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 25..
 */
public abstract class SipMessageHandler {
    private String transport="";
    private ServerInfo serverInfo=null;

    public SipMessageHandler(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public abstract void handle(Optional<DefaultSipMessage> maybeDefaultSipMessage);

    public void setTransport(String transport){
        this.transport=transport;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }
}
