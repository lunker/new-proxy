package org.lunker.new_proxy.stub;

import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipMessage;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 25..
 */
public interface SipMessageHandler {
    void handle(Optional<ProxySipMessage> generalSipMessage);
}
