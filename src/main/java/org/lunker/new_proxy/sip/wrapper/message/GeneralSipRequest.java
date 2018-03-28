package org.lunker.new_proxy.sip.wrapper.message;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import org.lunker.new_proxy.sip.session.ss.SipSessionKey;

import javax.sip.address.Address;
import javax.sip.header.ContactHeader;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.ListIterator;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class GeneralSipRequest extends GeneralSipMessage{

    private SIPRequest jainSipRequest=null;

    private GeneralSipRequest() {
    }

    public GeneralSipRequest(SIPMessage jainSipRequest, SipSessionKey sipSessionKey) {
        super(jainSipRequest, sipSessionKey);
    }

    public GeneralSipResponse createResponse(int statusCode) {
        return this.createResponse(statusCode,"");
    }

    public GeneralSipResponse createResponse(int statusCode, String reasonPhrase) {

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
            /*
            if (session != null && session.getCopyRecordRouteHeadersOnSubsequentResponses() && !this.isInitial() && "INVITE".equals(requestMethod)) {
                ListIterator recordRouteHeaders = request.getHeaders("Record-Route");

                while(recordRouteHeaders.hasNext()) {
                    RecordRouteHeader recordRouteHeader = (RecordRouteHeader)recordRouteHeaders.next();
                    response.addHeader(recordRouteHeader);
                }
            }
            */

            if("INVITE".equals(requestMethod)){
                ListIterator recordRouteHeaders = request.getHeaders("Record-Route");

                while(recordRouteHeaders.hasNext()) {
                    RecordRouteHeader recordRouteHeader = (RecordRouteHeader)recordRouteHeaders.next();
                    response.addHeader(recordRouteHeader);
                }
            }

            GeneralSipResponse generalSipResponse=new GeneralSipResponse((SIPResponse) response, this.sipSessionKey);
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
}
