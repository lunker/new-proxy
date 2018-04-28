package org.lunker.new_proxy.sip.wrapper.message;

/**
 * Created by dongqlee on 2018. 4. 28..
 */
public interface SipRequest extends SipMessage{
    AbstractSipMessage createCancel();

    AbstractSipMessage createResponse(int statusCode);
    AbstractSipMessage createResponse(int statusCode, String reasonPhrase);

    void getRequestURI();
    void setRequestURI();

}
