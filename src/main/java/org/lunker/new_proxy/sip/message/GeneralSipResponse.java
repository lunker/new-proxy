package org.lunker.new_proxy.sip.message;

import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class GeneralSipResponse extends SIPResponse implements GeneralSipMessage{

    private SIPRequest relatedRequest=null;
    private SIPResponse jainSipResponse=null;


    public GeneralSipResponse(SIPResponse jainSipResponse){
        this.jainSipResponse=jainSipResponse;
    }

    public GeneralSipResponse(SIPRequest relatedRequest) {
        this.relatedRequest=relatedRequest;
    }

    public GeneralSipResponse(SIPRequest relatedRequest, SIPResponse jainSipResponse) {
        this.relatedRequest = relatedRequest;
        this.jainSipResponse = jainSipResponse;
    }

    public SIPRequest getRelatedRequest() {
        return relatedRequest;
    }
}
