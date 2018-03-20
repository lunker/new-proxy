package org.lunker.new_proxy.sip.session.ss;

import org.lunker.new_proxy.stub.session.ss.SIPSessionKey;

import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class SIPSessionKeyImpl implements SIPSessionKey{

    private String fromTag="";
    private String toTag="";
    private String callId="";
    private String applicationSessionId="";
    private String generatedKey="";
    private String applicationName="";

    public SIPSessionKeyImpl() {

    }

    public SIPSessionKeyImpl(String fromTag, String toTag, String callId, String applicationSessionId) {
        this.fromTag = fromTag;
        this.toTag = toTag;
        this.callId = callId;
        this.applicationSessionId = applicationSessionId;
        this.generateKey();
    }

    public static SIPSessionKeyImpl create(FromHeader fromHeader, ToHeader toHeader, String callId, String applicationSessionId){
        return new SIPSessionKeyImpl(fromHeader.getTag(), toHeader.getTag(), callId, applicationSessionId);
    }

    @Override
    public String getApplicationName() {
        return null;
    }

    @Override
    public String getApplicationSessionId() {
        return null;
    }

    @Override
    public String getToTag() {
        return null;
    }

    @Override
    public void setToTag(String var1, boolean var2) {

    }

    @Override
    public String getCallId() {
        return null;
    }

    @Override
    public String getFromTag() {
        return null;
    }

    @Override
    public String getKey() {
        return this.generatedKey;
    }

    public void generateKey(){
        // set generatedKey value using fromTag, toTag, callId, applicationSEssonId,

        if(toTag.equals("")){
            this.generatedKey = "(" + this.fromTag + ";" + this.callId + ";" + this.applicationSessionId + ";" + this.applicationName + ")";
        }
        else{
            this.generatedKey = "(" + this.fromTag + ";" + this.toTag + ";" + this.callId + ";" + this.applicationSessionId + ";" + this.applicationName + ")";
        }
    }


}
