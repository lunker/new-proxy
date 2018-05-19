package org.lunker.new_proxy.stub;

import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 25..
 */
public abstract class SipMessageHandler {
    private String transport="";
//    private Map<String, Object> properties=null;

    public abstract void handle(Optional<DefaultSipMessage> maybeDefaultSipMessage);

    public void setTransport(String transport){
        this.transport=transport;
//        this.properties=properties;
    }
}
