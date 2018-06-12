package org.lunker.new_proxy.sip.wrapper.message.lb;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import org.junit.Test;
import org.lunker.new_proxy.sip.util.SipMessageFactory;

import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.address.AddressFactory;
import javax.sip.address.URI;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import java.text.ParseException;

import static org.junit.Assert.*;

public class LoadBalancerRequestTest {

    @Test
    public void createResponse() throws PeerUnavailableException, ParseException {
        SipFactory sipFactory = SipFactory.getInstance();
        MessageFactory messageFactory = sipFactory.createMessageFactory();

        SIPRequest sipRequest = (SIPRequest) messageFactory.createRequest(
                "INVITE sip:sipcallee00001@siptest.com SIP/2.0\n" +
                "Via: SIP/2.0/TCP 127.0.0.1:6062;branch=z9hG4bK-35400-1-6\n" +
                "From: sipcaller00001 <sip:sipcaller00001@siptest.com>;tag=35400SIPpTag001\n" +
                "To: sipcallee00001 <sip:sipcallee00001@siptest.com>\n" +
                "Call-ID: 1-35400@127.0.0.1\n" +
                "CSeq: 1 INVITE\n" +
                "Contact: <sip:sipcaller00001@127.0.0.1:6062;transport=TCP>\n" +
                "User-Agent: Moimstone\n" +
                "Max-Forwards: 70\n" +
                "Content-Type: application/sdp\n" +
                "Content-Length:   133\n" +
                "\n" +
                "v=0\n" +
                "o=user1 53655765 2353687637 IN IP4 127.0.0.1\n" +
                "s=-\n" +
                "c=IN IP4 127.0.0.1\n" +
                "t=0 0\n" +
                "m=audio 6001 RTP/AVP 0\n" +
                "a=rtpmap:0 PCMU/8000\n" +
                "a=sendrecv\n");

        LoadBalancerRequest lbRequest = new LoadBalancerRequest(sipRequest);
        LoadBalancerResponse lbResponse = lbRequest.createResponse(Response.TOO_MANY_HOPS);
        System.out.println(lbResponse);
    }
}