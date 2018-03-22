package org.lunker.new_proxy.sip.session.ss;

import javax.sip.header.FromHeader;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class SIPSessionKey {

    private String fromTag="";
    private String callId="";
    private String applicationSessionId="";
    private String generatedKey="";
    private String applicationName="";

    public SIPSessionKey() {

    }

    public SIPSessionKey(String fromTag, String callId, String applicationSessionId) {
        this.fromTag = fromTag;
        this.callId = callId;
        this.applicationSessionId = applicationSessionId;
        this.generatedKey=generateKey();
    }

    public static SIPSessionKey create(FromHeader fromHeader, String callId, String applicationSessionId){
        return new SIPSessionKey(fromHeader.getTag(), callId, applicationSessionId);
    }

    public String getKey() {
        return this.generatedKey;
    }

    public String generateKey(){
        // set generatedKey value using fromTag, toTag, callId, applicationSEssonId,
        return "(" + this.fromTag + ";" + this.callId + ";" + this.applicationSessionId + ";" + this.applicationName + ")";
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
            SIPSessionKey other=(SIPSessionKey) obj;

            if(!other.getKey().equals(this.getKey())){
                return false;
            }
            else{
                return true;
            }
        }
    }

    @Override
    public String toString() {
        return this.generatedKey;
    }
}
