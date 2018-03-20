package org.lunker.new_proxy.util;

import gov.nist.javax.sip.message.SIPMessage;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.sip.SIPHeaderParser;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class ProxyUtil {
    private static Registrar registrar=null;
    private static int MIN_CALLID_LENGTH=20;
    private static int MAX_CALLID_LENGTH=50;
    private static int MAX_TAG_LENGTH=30;
    private static Random random=null;
    private static int OPPORTUNITY=40;

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

    public static ChannelHandlerContext getTargetCtx(SIPMessage sipMessage){

        String targetAor="";
        String userAgent="";
        String userKey="";

        Registration registration=null;
        ChannelHandlerContext targetCtx=null;

        targetAor=sipMessage.getTo().getAddress().getURI().toString().split(":")[1];
        userAgent= SIPHeaderParser.getUserAent(sipMessage);
        userKey=targetAor+"_"+userAgent;

        targetCtx=registrar.getCtx(userKey);

        return targetCtx;
    }

    private String generateCallId(){
        return generateRandStr(MAX_CALLID_LENGTH);
    }

    private String generateTag(){
        return generateRandStr(MAX_TAG_LENGTH);
    }

    public static String generateBranchTag(){
        return generateRandStr(MAX_TAG_LENGTH);
    }

    /*
    public static String generateRandStr(int maxLength) {
        byte[] byteStr = new byte[maxLength];

//        StringBuilder stringBuilder=new StringBuilder();
        int chance = 0;
        byte ch = 0;

        for (int idx = 0; idx < maxLength; idx++) {
            chance = ThreadLocalRandom.current().nextInt(0, 40);

            if (chance < 10)
                ch = ((byte) (ThreadLocalRandom.current().nextInt(0, 25) + 'a'));
            else if (chance < 20)
                ch = ((byte) (ThreadLocalRandom.current().nextInt(0, 25) + 'A'));
            else if (chance < 30)
                ch = (byte) (ThreadLocalRandom.current().nextInt(0, 9));
            else
                ch = (byte) (ThreadLocalRandom.current().nextInt(0, 9));

            byteStr[idx]=ch;
        }

        return new String(byteStr);
    }
    */

    public static String generateRandStr(int maxLength) {
        StringBuilder stringBuilder=new StringBuilder();
        int chance=0;

        for (int idx=0; idx<maxLength; idx++){
            chance=random.nextInt(OPPORTUNITY);

            if(chance<10)
                stringBuilder.append((char)(ThreadLocalRandom.current().nextInt(0, 25) + 'a'));
            else if (chance<20)
                stringBuilder.append((char)(ThreadLocalRandom.current().nextInt(0, 25) + 'A'));
            else if(chance<35)
                stringBuilder.append(ThreadLocalRandom.current().nextInt(0, 9));
            else
                stringBuilder.append('_');
        }
        return stringBuilder.toString();
    }
}
