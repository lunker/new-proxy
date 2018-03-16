package org.lunker.new_proxy.stub.session;

import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public interface SIPApplicationSession {

    /** @deprecated */
    void encodeURI(URI var1);

    URL encodeURL(URL var1);

    String getApplicationName();

    Object getAttribute(String var1);

    Iterator<String> getAttributeNames();

    long getCreationTime();

    long getExpirationTime();

    String getId();

    boolean getInvalidateWhenReady();

    void setInvalidateWhenReady(boolean var1);

    long getLastAccessedTime();

    Iterator<?> getSessions();

    Iterator<?> getSessions(String var1);

    SipSession getSipSession(String var1);

    Object getSession(String var1, SipApplicationSession.Protocol var2);

    ServletTimer getTimer(String var1);

    Collection<ServletTimer> getTimers();

    void invalidate();

    boolean isReadyToInvalidate();

    boolean isValid();

    void removeAttribute(String var1);

    void setAttribute(String var1, Object var2);

    int setExpires(int var1);

    public static enum Protocol {
        HTTP,
        SIP;

        private Protocol() {
        }
    }


}
