package org.lunker.new_proxy.stub.session;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public interface SIPSessionKey {
    String getApplicationName();

    String getApplicationSessionId();

    String getToTag();

    void setToTag(String var1, boolean var2);

    String getCallId();

    String getFromTag();
}
