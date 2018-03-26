package org.lunker.new_proxy.sip;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sip.parser.StringMsgParser;
import org.lunker.new_proxy.sip.message.GeneralSipMessage;
import org.lunker.new_proxy.sip.message.GeneralSipRequest;
import org.lunker.new_proxy.sip.message.GeneralSipResponse;

import java.text.ParseException;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class SIPMessageBuilder {
    private StringMsgParser stringMsgParser=null;


    public SIPMessageBuilder() {
        stringMsgParser=new StringMsgParser();
    }

    public GeneralSipMessage parse(String message) throws ParseException{
        GeneralSipMessage generalSipMessage=null;
        SIPMessage jainSipMessage=stringMsgParser.parseSIPMessage(message.getBytes(), true, false, null);

        if(jainSipMessage instanceof SIPRequest){
            generalSipMessage=new GeneralSipRequest((SIPRequest) jainSipMessage);
        }
        else{
            generalSipMessage=new GeneralSipResponse((SIPResponse) jainSipMessage);
        }
        return generalSipMessage;
    }
}
