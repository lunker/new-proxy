package org.lunker.new_proxy.stub;

import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public interface AbstractSIPHandler {
    void handleRegister(GeneralSipMessage registerRequest);
    void handleInvite(GeneralSipMessage inviteRequest);
    void handleCancel(GeneralSipMessage request);
    void handleAck(GeneralSipMessage ackRequest);
    void handleBye(GeneralSipMessage byeRequest);
}
