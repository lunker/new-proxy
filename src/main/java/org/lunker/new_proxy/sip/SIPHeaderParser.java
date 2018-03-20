package org.lunker.new_proxy.sip;

import gov.nist.javax.sip.message.SIPMessage;

import javax.sip.header.Header;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class SIPHeaderParser {
    public static String getUserAent(SIPMessage sipMessage){
        Header userAgentHeader=sipMessage.getHeader("User-Agent");
        String userAgent=userAgentHeader.toString().split(":")[1];
        userAgent=userAgent.substring(0, userAgent.length()-2);
        return userAgent;
    }
}
