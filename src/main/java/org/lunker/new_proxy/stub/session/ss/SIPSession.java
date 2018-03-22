package org.lunker.new_proxy.stub.session.ss;

import org.lunker.new_proxy.sip.session.ss.SIPSessionKey;
import org.lunker.new_proxy.stub.session.sas.SIPApplicationSession;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public interface SIPSession {
//
//    /**
//     * Returns the object bound with the specified name in this session, or null if no object is bound under the name.
//     */
//    java.lang.Object getAttribute(java.lang.String name);
//
//    /**
//     * Returns an Enumeration over the String objects containing the names of all the objects bound to this session.
//     */
//    Enumeration<String> getAttributeNames();
//
//    /**
//     * Returns the Call-ID for this SipSession. This is the value of the Call-ID header for all messages belonging to this session.
//     */
//    java.lang.String getCallId();
//
//    /**
//     * Returns the time when this session was created, measured in milliseconds since midnight January 1, 1970 GMT.
//     */
//    long getCreationTime();
//
//    /**
//     * Returns a string containing the unique identifier assigned to this session. The identifier is assigned by the servlet container and is implementation dependent.
//     */
//    java.lang.String getId();
//
//    /**
//     * Returns true if the container will notify the application when this SipSession is in the ready-to-invalidate state.
//     * @return value of the invalidateWhenReady flag
//     * @throws IllegalStateException if this method is called on an invalidated session
//     * @since 1.1
//     */
//    boolean getInvalidateWhenReady();
//
//    /**
//     * Specifies whether the container should notify the application when the SipSession
//     * is in the ready-to-invalidate state as defined above.
//     * The container notifies the application using the SipSessionListener.sessionReadyToInvalidate callback.
//     * @param invalidateWhenReady if true, the container will observe this session and
//     * notify the application when it is in the ready-to-invalidate state.
//     * The session is not observed if the flag is false.
//     * The default is true for v1.1 applications and false for v1.0 applications.
//     * @throws IllegalStateException if this method is called on an invalidated session
//     * @since 1.1
//     */
//    void setInvalidateWhenReady(boolean invalidateWhenReady);
//
//    /**
//     * Returns the last time the client sent a request associated with this session, as the number of milliseconds since midnight January 1, 1970 GMT. Actions that your application takes, such as getting or setting a value associated with the session, do not affect the access time.
//     */
//    long getLastAccessedTime();
//
//    /**
//     * Returns the Address identifying the local party. This is the value of the From header of locally initiated requests in this leg.
//     */
//    javax.servlet.sip.Address getLocalParty();
//
//    /**
//     * This method allows the application to obtain the region it was invoked in for this SipSession.
//     * This information helps the application to determine the location of the subscriber
//     * returned by <code>SipSession.getSubscriberURI()</code>
//     * <p> If this SipSession is created when this servlet receives an initial request,
//     * this method returns the region in which this servlet is invoked.
//     *  The <code>SipApplicationRoutingRegion</code> is only available if this SipSession received an initial request.
//     *  Otherwise, this method throws IllegalStateException.<P>
//     *
//     * @return The routing region (ORIGINATING, NEUTRAL, TERMINATING or their sub-regions)
//     * @throws IllegalStateException     if this method is called on an invalidated session
//     * @since 1.1
//     */
//    javax.servlet.sip.ar.SipApplicationRoutingRegion getRegion();
//
//    /**
//     * Returns the Address identifying the remote party. This is the value of the To header of locally initiated requests in this leg.
//     */
//    javax.servlet.sip.Address getRemoteParty();
//
//    /**
//     * Returns the ServletContext to which this session belongs.
//     * By definition, there is one ServletContext per sip (or web) module per JVM.
//     * Though, a SipSession belonging to a distributed application deployed
//     * to a distributed container may be available across JVMs,
//     * this method returns the context that is local to the JVM on which it was invoked.
//     *
//     * @return ServletContext object for the sip application
//     * @since 1.1
//     */
//    javax.servlet.ServletContext getServletContext();
//
//    /**
//     * Returns the current SIP dialog state, which is one of INITIAL, EARLY, CONFIRMED, or TERMINATED. These states are defined in RFC3261.
//     */
//    javax.servlet.sip.SipSession.State getState();
//
//    /**
//     * Returns the URI of the subscriber for which this application is invoked to serve. This is only available if this SipSession received an initial request. Otherwise, this method throws IllegalStateException.
//     * @throws IllegalStateException if this method is called on an invalidated session
//     */
//    javax.servlet.sip.URI getSubscriberURI();
//
//    /**
//     * Invalidates this session and unbinds any objects bound to it. A session cannot be invalidate if it is in the EARLY or CONFIRMED state, or if there exist ongoing transactions where a final response is expected. One exception is if this session has an associated unsupervised proxy, in which case the session can be invalidate even if transactions are ongoing.
//     */
//    void invalidate();
//
//    /**
//     * Returns true if this SipSession is valid, false otherwise. The SipSession can be invalidated by calling the method
//     * on it. Also the SipSession can be invalidated by the container when either the associated
//     * times out or
//     * is invoked.
//     */
//    boolean isValid();
//
//    /**
//     * Returns true if this session is in a ready-to-invalidate state. A SipSession is in the ready-to-invalidate state under any of the following conditions:
//     *
//     * 1. The SipSession transitions to the TERMINATED state.
//     * 2. The SipSession transitions to the COMPLETED state when it is acting as a non-record-routing proxy.
//     * 3. The SipSession acting as a UAC transitions from the EARLY state back to the INITIAL state on account of receiving a non-2xx final response and has not initiated any new requests (does not have any pending transactions).
//     *
//     * @return if the session is in ready-to-invalidate state, false otherwise
//     * @throws IllegalStateException if this method is called on an invalidated session
//     * @since 1.1
//     */
//    boolean isReadyToInvalidate();
//
//    /**
//     * Removes the object bound with the specified name from this session. If the session does not have an object bound with the specified name, this method does nothing.
//     */
//    void removeAttribute(java.lang.String name);
//
//    /**
//     * Binds an object to this session, using the name specified. If an object of the same name is already bound to the session, the object is replaced.
//     */
//    void setAttribute(java.lang.String name, java.lang.Object attribute);
//
//    /**
//     * Sets the handler for this SipSession.
//     * This method can be used to explicitly specify the name of the servlet which
//     * should handle all subsequently received messages for this SipSession.
//     * The servlet must belong to the same application (i.e. same ServletContext) as the caller.
//     *
//     *
//     */
//    void setHandler(java.lang.String name) throws javax.servlet.ServletException;
//
//    /**
//     * In multi-homed environment this method can be used to select the outbound interface
//     * and port number to use for proxy branches.
//     * The specified address must be the address of one of the configured outbound interfaces.
//     * The set of SipURI objects which represent the supported outbound interfaces can be obtained from the servlet context attribute named javax.servlet.sip.outboundInterfaces.
//     *
//     * The port is interpreted as an advice by the app to the container.
//     * If the port of the socket address has a non-zero value,
//     * the container will make a best-effort attempt to use it as the source port number for UDP packets,
//     * or as a source port number for TCP connections it originates.
//     * If the port is not available, the container will use its default port allocation scheme.
//     *
//     * Invocation of this method also impacts the system headers generated by the container for this Proxy,
//     * such as the Record-Route header (getRecordRouteURI()),
//     * the Via and the Contact header.
//     * The IP address part of the socket address is used to construct these system headers.
//     * @param address the socket address representing the outbound interface to use when forwarding requests with this proxy
//     * @throws NullPointerException on null address
//     * @throws IllegalArgumentException if the address is not understood by the container as one of its outbound interface
//     */
//    void setOutboundInterface(java.net.InetAddress address);
//
//    /**
//     * In multi-homed environment this method can be used to select the outbound interface
//     * and port number to use for proxy branches.
//     * The specified address must be the address of one of the configured outbound interfaces.
//     * The set of SipURI objects which represent the supported outbound interfaces can be obtained from the servlet context attribute named javax.servlet.sip.outboundInterfaces.
//     *
//     * Invocation of this method also impacts the system headers generated by the container for this Proxy,
//     * such as the Record-Route header (getRecordRouteURI()),
//     * the Via and the Contact header.
//     * The IP address part of the socket address is used to construct these system headers.
//     * @param address the address which represents the outbound interface
//     * @throws NullPointerException on null address
//     * @throws IllegalArgumentException if the address is not understood by the container as one of its outbound interface
//     * @throws IllegalStateException if this method is called on an invalidated session
//     */
//    void setOutboundInterface(java.net.InetSocketAddress address);
//
//    /**
//     * Possible SIP dialog states from SipSession FSM.
//     * @since 1.1
//     *
//     */
//    public enum State{
//        INITIAL,
//        EARLY,
//        CONFIRMED,
//        TERMINATED;
//    }


    SIPApplicationSession getSIPApplicationSession();
    SIPSessionKey getSipSessionkey();
    void addAttribute(String key, Object value);

}
