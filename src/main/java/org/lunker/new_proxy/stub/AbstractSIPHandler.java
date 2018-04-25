package org.lunker.new_proxy.stub;

import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
@Deprecated
public interface AbstractSIPHandler {
    GeneralSipMessage handleRegister(GeneralSipMessage registerRequest);
    GeneralSipMessage handleInvite(GeneralSipMessage inviteRequest);
    GeneralSipMessage handleCancel(GeneralSipMessage cancelRequest);
    GeneralSipMessage handleAck(GeneralSipMessage ackRequest);
    GeneralSipMessage handleBye(GeneralSipMessage byeRequest);
}
