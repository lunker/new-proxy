package org.lunker.new_proxy.sip.wrapper.message.proxy;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import org.lunker.new_proxy.sip.session.ss.SipSessionKey;
import org.lunker.new_proxy.sip.wrapper.message.SipRequest;

import javax.sip.address.Address;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.ListIterator;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class ProxySipRequest extends ProxySipMessage implements SipRequest {

    private SIPRequest jainSipRequest=null;

    private ProxySipRequest() {

    }

    public ProxySipRequest(SIPMessage jainSipRequest, SipSessionKey sipSessionKey) {
        super(jainSipRequest, sipSessionKey);
    }

    public ProxySipResponse createResponse(int statusCode) {
        return this.createResponse(statusCode,"");
    }

    public ProxySipResponse createResponse(int statusCode, String reasonPhrase) {

        try {
            Request request = (Request)this.message;
            Response response = this.sipMessageFactory.createResponse(statusCode, request);
            if (reasonPhrase != null) {
                response.setReasonPhrase(reasonPhrase);
            }

            String requestMethod = this.getMethod();
            if (statusCode > 100  && statusCode <= 606) {

                // set to tag
                ToHeader toHeader = (ToHeader)response.getHeader("To");

                /*
                if (toHeader.getTag() == null) {
                    Dialog dialog = transaction.getDialog();
                    if (session != null && dialog != null && dialog.getLocalTag() != null && dialog.getLocalTag().length() > 0 && session.getKey().getToTag() != null && session.getKey().getToTag().length() > 0) {
                        if (!dialog.getLocalTag().equals(session.getKey().getToTag())) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("setting session ToTag: " + session.getKey().getToTag());
                            }

                            toHeader.setTag(session.getKey().getToTag());
                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("setting dialog LocalTag: " + dialog.getLocalTag());
                            }

                            toHeader.setTag(dialog.getLocalTag());
                        }
                    } else if (session != null && session.getSipApplicationSession() != null) {
                        MobicentsSipApplicationSessionKey sipAppSessionKey = session.getSipApplicationSession().getKey();
                        MobicentsSipSessionKey sipSessionKey = session.getKey();
                        synchronized(this) {
                            String toTag = sipSessionKey.getToTag();
                            if (logger.isDebugEnabled()) {
                                logger.debug("sipSessionKey ToTag : " + toTag);
                            }

                            if (toTag == null) {
                                toTag = ApplicationRoutingHeaderComposer.getHash(this.sipFactoryImpl.getSipApplicationDispatcher(), sipSessionKey.getApplicationName(), sipAppSessionKey.getId());
                                session.getKey().setToTag(toTag, false);
                            }

                            if (logger.isDebugEnabled()) {
                                logger.debug("setting ToTag: " + toTag);
                            }

                            toHeader.setTag(toTag);
                        }
                    } else {
                        toHeader.setTag(Integer.toString((new Random()).nextInt(10000000)));
                    }
                }
                */

                // Set toTag
                if(toHeader.getTag() == null){
                    String applicatoinSessionId=this.sipSessionKey.getApplicationSessionId();
                    toHeader.setTag(applicatoinSessionId);
                }
                // end set to-tag

                // set contact header
                boolean setContactHeader = true;
                if (statusCode >= 300 && statusCode < 400 || statusCode == 485 || "REGISTER".equals(requestMethod) || "OPTIONS".equals(requestMethod) || "BYE".equals(requestMethod) || "CANCEL".equals(requestMethod) || "PRACK".equals(requestMethod) || "MESSAGE".equals(requestMethod) || "PUBLISH".equals(requestMethod)) {
                    setContactHeader = false;
                }

                if (setContactHeader) {
                    /*
                    ContactHeader contactHeader = JainSipUtils.createContactHeader(super.sipFactoryImpl.getSipNetworkInterfaceManager(), request, (String)null, (String)null, outboundInterface);
                    String transport = "tcp";
                    if (session != null && session.getTransport() != null) {
                        transport = session.getTransport();
                    }
                    */

                    Address address=this.sipMessageFactory.getAddressFactory().createAddress("10.0.1.202:10010");
                    ContactHeader contactHeader=this.sipMessageFactory.getHeaderFactory().createContactHeader(address);
                    contactHeader.setParameter("transport", "tcp");

                    response.setHeader(contactHeader);
                }
            }

            // Set Record-Route
            if ("INVITE".equals(requestMethod)) {
                ListIterator recordRouteHeaders = request.getHeaders("Record-Route");

                while(recordRouteHeaders.hasNext()) {
                    RecordRouteHeader recordRouteHeader = (RecordRouteHeader)recordRouteHeaders.next();
                    response.addHeader(recordRouteHeader);
                }
            }

            ProxySipResponse generalSipResponse=new ProxySipResponse((SIPResponse) response, this.sipSessionKey);
            return generalSipResponse;

                /*
                SipServletResponseImpl newSipServletResponse = (SipServletResponseImpl)this.sipFactoryImpl.getMobicentsSipServletMessageFactory().createSipServletResponse(response, (Transaction)(validate ? (ServerTransaction)transaction : transaction), session, this.getDialog(), hasBeenReceived, false);
                newSipServletResponse.setOriginalRequest(this);
                if (!"PRACK".equals(requestMethod) && statusCode >= 200 && statusCode <= 606) {
                    this.isFinalResponseGenerated = true;
                }

                if (statusCode >= 100 && statusCode < 200) {
                    this.is1xxResponseGenerated = true;
                }

                return newSipServletResponse;
                */

        } catch (ParseException var19) {
            throw new IllegalArgumentException("Bad status code " + statusCode, var19);
        }
    }// end method

    public void setContent(Object content, String contentType) {
//        this.checkContentType(contentType);

        try{
            ContentTypeHeader contentTypeHeader=null;
            contentTypeHeader=(ContentTypeHeader)this.message.getHeader("Content-Type");
            contentTypeHeader.setContentType("application");
            contentTypeHeader.setContentSubType("sdp");

            this.message.setContent(content, contentTypeHeader);
        }
        catch (Exception e){
            e.printStackTrace();
        }

//
//
//        if (contentType != null && contentType.length() > 0) {
//            this.addHeader("Content-Type", contentType);
//            ContentTypeHeader contentTypeHeader = (ContentTypeHeader)this.message.getHeader("Content-Type");
//            String charset = this.getCharacterEncoding();
//
//            try {
//                if (contentType.contains("multipart") && content instanceof Multipart) {
//                    Multipart multipart = (Multipart)content;
//                    OutputStream os = new ByteArrayOutputStream();
//                    multipart.writeTo(os);
//                    this.message.setContent(os.toString(), contentTypeHeader);
//                } else {
//                    if (content instanceof String && charset != null) {
//                        new String("testEncoding".getBytes(charset));
//                        new String(((String)content).getBytes());
//                    }
//
//                    this.message.setContent(content, contentTypeHeader);
//                }
//            } catch (UnsupportedEncodingException var7) {
//                throw var7;
//            } catch (Exception var8) {
//                throw new IllegalArgumentException("Parse error reading content " + content + " with content type " + contentType, var8);
//            }
//        }

    }

    private void checkContentType(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("the content type cannot be null");
        } else {
            int indexOfSlash = contentType.indexOf("/");
            if (indexOfSlash != -1) {
                /*
                if (!JainSipUtils.IANA_ALLOWED_CONTENT_TYPES.contains(contentType.substring(0, indexOfSlash))) {
                    throw new IllegalArgumentException("the given content type " + contentType + " is not allowed");
                }
                */
                throw new IllegalArgumentException("the given content type " + contentType + " is not allowed");
            } else {
                throw new IllegalArgumentException("the given content type " + contentType + " is not allowed");
            }

        }
    }

    public String getCharacterEncoding() {
        if (this.message.getContentEncoding() != null) {
            return this.message.getContentEncoding().getEncoding();
        } else {
            ContentTypeHeader cth = (ContentTypeHeader)this.message.getHeader("Content-Type");
            return cth == null ? null : cth.getParameter("charset");
        }
    }

    public Object getContent(){
        return this.message.getContent();
    }
}
