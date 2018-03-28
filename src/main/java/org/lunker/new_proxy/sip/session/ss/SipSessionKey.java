package org.lunker.new_proxy.sip.session.ss;

import gov.nist.javax.sip.message.SIPMessage;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;

import javax.sip.header.FromHeader;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class SipSessionKey {

    private String fromTag="";
    private String toTag="";
    private String callId="";
    private String applicationSessionId="";
    private String generatedKey="";
    private String applicationName="";

    private SipSessionKey() {

    }

    public SipSessionKey(SIPMessage sipMessage, String sipApplicationSessionId) {
        this.fromTag=sipMessage.getFrom().getTag();
        this.callId=sipMessage.getCallId().getCallId();
        this.applicationSessionId=sipApplicationSessionId;
        generateKey();
    }

    public SipSessionKey(GeneralSipMessage sipMessage, String sipApplicationSessionId) {
        this.fromTag=sipMessage.getFrom().getTag();
        this.callId=sipMessage.getCallId();
        this.applicationSessionId=sipApplicationSessionId;
        generateKey();
    }

    // FIXME
    public static SipSessionKey create(FromHeader fromHeader, String callId, String applicationSessionId){
        // return new SipSessionKey(fromHeader.getTag(), callId, applicationSessionId);
        return null;
    }

    public String getKey() {
        return this.generatedKey;
    }

    public void generateKey(){
        if (this.toTag != null) {
            this.generatedKey = "(" + this.fromTag + ";" + this.toTag + ";" + this.callId + ";" + this.applicationSessionId + ";" + this.applicationName + ")";
        } else {
            this.generatedKey = "(" + this.fromTag + ";" + this.callId + ";" + this.applicationSessionId + ";" + this.applicationName + ")";
        }
//        // set generatedKey value using fromTag, toTag, callId, applicationSEssonId,
//        return "(" + this.fromTag + ";" + this.callId + ";" + this.applicationSessionId + ";" + this.applicationName + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            SipSessionKey other=(SipSessionKey) obj;

            if(!other.getKey().equals(this.getKey())){
                return false;
            }
            else{
                return true;
            }
        }
    }

    public String getApplicationSessionId() {
        return applicationSessionId;
    }

    @Override
    public String toString() {
        return this.generatedKey;
    }
}
