package org.lunker.new_proxy.sip.wrapper.message.lb;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPResponse;
import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipRequest;

import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * Created by dongqlee on 2018. 4. 28..
 * Modified by hoh
 */
public class LoadBalancerRequest extends DefaultSipRequest {

    public LoadBalancerRequest(SIPMessage sipMessage) {
        super(sipMessage);
    }

    public LoadBalancerResponse createResponse(int statusCode) throws ParseException {
        Configuration configuration = Configuration.getInstance();
        Request request = (Request) this.message;
        Response response = this.sipMessageFactory.createResponse(statusCode, request); // could throw ParseException
        return new LoadBalancerResponse((SIPResponse) response);
    }

}
