package org.lunker.new_proxy.stub;

import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipMessage;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
@Deprecated
public interface AbstractSIPHandler {
    ProxySipMessage handleRegister(ProxySipMessage registerRequest);
    ProxySipMessage handleInvite(ProxySipMessage inviteRequest);
    ProxySipMessage handleCancel(ProxySipMessage cancelRequest);
    ProxySipMessage handleAck(ProxySipMessage ackRequest);
    ProxySipMessage handleBye(ProxySipMessage byeRequest);
}
