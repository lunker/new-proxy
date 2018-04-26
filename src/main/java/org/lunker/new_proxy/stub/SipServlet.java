package org.lunker.new_proxy.stub;

import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 25..
 */
public interface SipServlet {
    void handle(Optional<GeneralSipMessage> generalSipMessage);
}
