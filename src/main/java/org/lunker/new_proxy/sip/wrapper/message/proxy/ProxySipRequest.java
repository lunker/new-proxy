package org.lunker.new_proxy.sip.wrapper.message.proxy;

import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.model.Constants;
import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.sip.B2BUAHelper;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipRequest;
import org.lunker.new_proxy.sip.wrapper.message.Sessionable;

import javax.sip.address.Address;
import javax.sip.header.ContactHeader;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class ProxySipRequest extends DefaultSipRequest implements Sessionable{
    private B2BUAHelper b2BUAHelper=null;
    private ServerInfo serverInfo=null;

    public ProxySipRequest(SIPMessage jainSipRequest) {
        super(jainSipRequest);
    }

    public ProxySipResponse createResponse(int statusCode) {
        return this.createResponse(statusCode,SIPResponse.getReasonPhrase(statusCode));
    }

    public ProxySipResponse createResponse(int statusCode, String reasonPhrase) {
        try {
            Configuration configuration=Configuration.getInstance();
            Request request = (Request)this.message;
            Response response = this.sipMessageFactory.createResponse(statusCode, request);
            String transport=((SipUri)((SIPRequest) request).getContactHeader().getAddress().getURI()).getTransportParam();
            Map<String, Object> serverConfigMap=configuration.getConfigMap(transport);

            String serverHost="";
            int serverPort=0;

            serverHost=(String) serverConfigMap.get(Constants.HOST);
            serverPort=(int) serverConfigMap.get(Constants.PORT);

            if (reasonPhrase != null) {
                response.setReasonPhrase(reasonPhrase);
            }

            String requestMethod = this.getMethod();
            if (statusCode > 100  && statusCode <= 606) {

                // set to tag
                ToHeader toHeader = (ToHeader)response.getHeader("To");

                // Set toTag
                if(toHeader.getTag() == null){

                    //  TODO(lunker): using SipSession
//                    String applicatoinSessionId=this.sipSessionKey.getApplicationSessionId();
//                    toHeader.setTag(applicatoinSessionId);

                    // Generate ToTag
                    this.sipMessageFactory.generateTag(getCallId(), "");
                }
                // end set to-tag

                // set contact header
                boolean setContactHeader = true;
                if (statusCode >= 300 && statusCode < 400 || statusCode == 485 || "REGISTER".equals(requestMethod) || "OPTIONS".equals(requestMethod) || "BYE".equals(requestMethod) || "CANCEL".equals(requestMethod) || "PRACK".equals(requestMethod) || "MESSAGE".equals(requestMethod) || "PUBLISH".equals(requestMethod)) {
                    setContactHeader = false;
                }

                if (setContactHeader) {
                    /* // TODO:
                    ContactHeader contactHeader = JainSipUtils.createContactHeader(super.sipFactoryImpl.getSipNetworkInterfaceManager(), request, (String)null, (String)null, outboundInterface);
                    String transport = "tcp";
                    if (session != null && session.getTransport() != null) {
                        transport = session.getTransport();
                    }
                    */


                    // Proxy ServerInfo..
                    Address address=this.sipMessageFactory.getAddressFactory().createAddress(String.format("%s:%d", serverHost, serverPort));
                    ContactHeader contactHeader=this.sipMessageFactory.getHeaderFactory().createContactHeader(address);
                    contactHeader.setParameter("transport", transport);

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

//            ProxySipResponse generalSipResponse=new ProxySipResponse((SIPResponse) response, this.sipSessionKey);
            ProxySipResponse generalSipResponse=new ProxySipResponse((SIPResponse) response);
            return generalSipResponse;
        } catch (ParseException var19) {
            throw new IllegalArgumentException("Bad status code " + statusCode, var19);
        }
    }// end method

    @Override
    public ProxySipRequest clone() {
        SIPRequest sipRequest=(SIPRequest) this.message.clone();
        return new ProxySipRequest(sipRequest);
    }
}
