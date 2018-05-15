package org.lunker.new_proxy.sip.wrapper.message;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPResponse;

/**
 * Created by dongqlee on 2018. 4. 28..
 */
public class DefaultSipResponse extends DefaultSipMessage {

    private SIPResponse response=null;

    public DefaultSipResponse(SIPMessage sipMessage) {
        super(sipMessage);

        this.response=(SIPResponse) this.message;
    }

    public int getStatusCode(){
        return response.getStatusCode();
    }
}
