package org.lunker.new_proxy.sip.wrapper.message;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPResponse;
import org.lunker.new_proxy.sip.session.ss.SipSessionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class GeneralSipResponse extends GeneralSipMessage{

    private Logger logger= LoggerFactory.getLogger(GeneralSipResponse.class);

    public GeneralSipResponse(SIPMessage sipMessage, SipSessionKey sipSessionKey) {
        super(sipMessage, sipSessionKey);
    }

    public int getStatusCode(){
        return ((SIPResponse) message).getStatusCode();
    }
}
