package org.lunker.new_proxy.stub;

import gov.nist.javax.sip.message.SIPRequest;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public interface AbstractSIPHandler {
    void handleRegister(SIPRequest registerRequest);
    void handleInvite(SIPRequest inviteRequest);
    void handleCancel(SIPRequest request);
    void handleAck(SIPRequest ackRequest);
    void handleBye(SIPRequest byeRequest);
}
