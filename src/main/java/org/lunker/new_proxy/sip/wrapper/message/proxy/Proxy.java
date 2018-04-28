package org.lunker.new_proxy.sip.wrapper.message.proxy;

import org.lunker.new_proxy.sip.wrapper.message.AbstractSipMessage;

/**
 * Created by dongqlee on 2018. 4. 28..
 */
public interface Proxy {

    abstract AbstractSipMessage createCancel();

    abstract AbstractSipMessage createResponse(int statusCode);
    abstract AbstractSipMessage createResponse(int statusCode, String reasonPhrase);

    default void createSession(){

    }
}
