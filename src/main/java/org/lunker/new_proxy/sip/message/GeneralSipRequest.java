package org.lunker.new_proxy.sip.message;

import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class GeneralSipRequest extends SIPRequest implements GeneralSipMessage{

    private SIPRequest jainSipRequest=null;

    private GeneralSipRequest() {
    }

    public GeneralSipRequest(SIPRequest jainSipRequest) {
        this.jainSipRequest=jainSipRequest;
    }

    public GeneralSipResponse createResponse(int statusCode) {
        SIPResponse jainSipResponse=super.createResponse(statusCode);

        return new GeneralSipResponse(this.jainSipRequest, jainSipResponse);
    }

}
