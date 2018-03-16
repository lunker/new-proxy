package org.lunker.new_proxy.server;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lunker.new_proxy.stub.AbstractSIPHandler;
import org.lunker.new_proxy.util.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.WWWAuthenticateHeader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class SIPHandler extends ChannelInboundHandlerAdapter implements AbstractSIPHandler{
    private Logger logger= LoggerFactory.getLogger(SIPHandler.class);

    private ChannelHandlerContext ctx=null;
    private javax.sip.SipFactory sipFactory=null;
    private HeaderFactory headerFactory=null;

    public SIPHandler() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Channel Active!!!!!!!!!!!!!!!!!!!!!!!!!!");
        this.ctx=ctx;
        try{
            this.sipFactory=javax.sip.SipFactory.getInstance();
            this.headerFactory=sipFactory.createHeaderFactory();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("In SIPHandler");
        logger.info("[RECEIVED]\n" + ((SIPRequest) msg).toString());

        SIPMessage sipMessage=(SIPMessage) msg;

        if(sipMessage instanceof SIPRequest){
            String method=((SIPRequest) sipMessage).getMethod();
            if(method.equals(SIPRequest.REGISTER))
                this.handleRegister((SIPRequest) sipMessage);
        }
        else if(sipMessage instanceof SIPResponse){

        }
    }

    public void handleResponse(SIPResponse response){

        int statusCode=response.getStatusCode();


        if(statusCode==SIPResponse.UNAUTHORIZED){
            // handle register
//            String authorization	= request.getHeader(Constants.SH_AUTHORIZATION);
        }

    }
    public  String getNonce()
    {
        Calendar cal = Calendar.getInstance();
        java.util.Date currentTime = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        String nonce = encryptString(formatter.format(currentTime));
        return nonce;
    }
    public  String encryptString(String param)
    {
        StringBuffer md5 = new StringBuffer();

        try
        {
            byte[] digest = java.security.MessageDigest.getInstance("MD5").digest(param.getBytes());
            for (int i = 0; i < digest.length; i++)
            {
                md5.append(Integer.toString((digest[i] & 0xf0) >> 4, 16));
                md5.append(Integer.toString(digest[i] & 0x0f, 16));
            }
        }
        catch(java.security.NoSuchAlgorithmException ne)
        {
            ne.printStackTrace();
        }
        return md5.toString();
    }

    @Override
    public void handleRegister(SIPRequest registerRequest) {
//        Optional<Header> authorization = Optional.ofNullable(registerRequest.getHeader("Authorization"));
        Header authHeader= registerRequest.getHeader("Authorization");
        String authorization="";
        SIPResponse sipResponse=null;

        if(authHeader==null){

            // non-auth
            sipResponse=registerRequest.createResponse(SIPResponse.UNAUTHORIZED);
            String domain=registerRequest.getFrom().getAddress().getURI().toString().split("@")[1];

            try{
                WWWAuthenticateHeader wwwAuthenticateHeader=this.headerFactory.createWWWAuthenticateHeader("Digest");
                wwwAuthenticateHeader.setAlgorithm("MD5");
                wwwAuthenticateHeader.setQop("auth");
                wwwAuthenticateHeader.setNonce(getNonce());
                wwwAuthenticateHeader.setRealm(domain);

                sipResponse.addHeader(wwwAuthenticateHeader);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        else{
            // do auth
            authorization=authHeader.toString();
            AuthUtil authUtil=new AuthUtil(authorization);
            authUtil.setPassword("aaaaaa");

            if(authUtil.isEqualHA()){
                // Auth success
                logger.warn("REGISTER Success");
                sipResponse=registerRequest.createResponse(SIPResponse.OK);
            }
            else{
                logger.warn("REGISTER Fail");
            }
        }

        // fire response
        ctx.fireChannelRead(sipResponse.toString());
    }

    @Override
    public void handleInvite(SIPRequest request) {

    }

    @Override
    public void handleCancel(SIPRequest request) {

    }
}
