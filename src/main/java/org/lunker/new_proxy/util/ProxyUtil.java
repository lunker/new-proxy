package org.lunker.new_proxy.util;

import gov.nist.javax.sip.message.SIPMessage;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;

import java.util.Random;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class ProxyUtil {
    private static Registrar registrar=null;
    private static int MIN_CALLID_LENGTH=20;
    private static int MAX_CALLID_LENGTH=50;
    private static int MAX_TAG_LENGTH=30;

    private static int OPPORTUNITY=40;
    private static Random random=null;

    static {
        registrar=Registrar.getInstance();
        random=new Random();
    }

    public static String getUserKey(SIPMessage sipMessage){
        return "";
    }

    public static ChannelHandlerContext getCtx(SIPMessage sipMessage){
        return null;
    }

    public static ChannelHandlerContext getTargetCtx(GeneralSipMessage sipMessage){

        String targetAor="";
        String userAgent="";
        String userKey="";

        Registration registration=null;
        ChannelHandlerContext targetCtx=null;

        targetAor=sipMessage.getTo().getAddress().getURI().toString().split(":")[1];
//        userAgent= SIPHeaderParser.getUserAent(sipMessage);
        userKey=targetAor;

        targetCtx=registrar.getCtx(userKey);

        return targetCtx;
    }


    public static String createSIPApplicationSessionId(){
        return "";
    }
}
