package org.lunker.proxy.sip.pre_process;

import gov.nist.javax.sip.message.SIPRequest;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipRequest;
import org.lunker.proxy.core.Message;
import org.lunker.proxy.core.ProcessState;
import org.lunker.proxy.core.ProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.Header;
import javax.sip.header.MaxForwardsHeader;

/**
 * Created by dongqlee on 2018. 5. 1..
 */
public class ProxyPreHandler implements ProxyHandler {
    private Logger logger= LoggerFactory.getLogger(ProxyPreHandler.class);

    @Override
    public Message handle(Message message) {
        if(message.getProcessState() != ProcessState.PRE) {
            //TODO: State check
            return message;
        }

        if(logger.isDebugEnabled())
            logger.debug("In ProxyPreHandler");

        validateRequest(message.getOriginalMessage());

        message.setProcessState(ProcessState.IN);

        return message;
    }

    /**
     * Validate sip request according to rfc 3261 section 16.3
     */
    public boolean validateRequest(DefaultSipMessage defaultSipMessage){
        checkSipUriScheme(defaultSipMessage);
        checkMaxForwards(defaultSipMessage);
        checkRequestLoop(defaultSipMessage);

        return false;
    }

    private boolean checkSipUriScheme(DefaultSipMessage defaultSipMessage){
        if(defaultSipMessage instanceof DefaultSipRequest)
            return ((DefaultSipRequest)defaultSipMessage).getRequestURI().isSipURI();
        return false;
    }

    /**
     * Check Max-Forwards Header
     * @param defaultSipMessage
     * @return
     */
    private boolean checkMaxForwards(DefaultSipMessage defaultSipMessage){
        MaxForwardsHeader maxForwardsHeader=defaultSipMessage.getMaxForwards();
        boolean isValidate=false;

        if(maxForwardsHeader==null){
            isValidate=false;
        }
        else{
            int maxForwards=0;
            maxForwards=maxForwardsHeader.getMaxForwards();

            if(maxForwards > 0){
                isValidate=true;
            }
            else if(maxForwards == 0 ){
                if(SIPRequest.INFO.equals(defaultSipMessage.getMethod())){
                    isValidate=true;
                }
                else{
                    isValidate=false;
                }
            }
            else{
                // TODO: create 483 response

            }
        }

        return isValidate;
    }

    /**
     * Check whether received sip request is looped
     * @param defaultSipMessage
     * @return
     */
    private boolean checkRequestLoop(DefaultSipMessage defaultSipMessage){
        String branch=defaultSipMessage.getTopmostVia().getBranch();
        String targetBranch="";

        return false;
    }

    // TODO
    private boolean checkProxyRequire(DefaultSipMessage defaultSipMessage){
        Header proxyRequireHeader=defaultSipMessage.getHeader("Proxy-Require");

        if(proxyRequireHeader==null){

        }
        else{

        }

        return false;
    }

    // TODO
    private boolean checkProxyAuthorization(DefaultSipMessage defaultSipMessage){

        Header proxyAuthorizationHeader=defaultSipMessage.getHeader("Proxy-Authorization");

        if(proxyAuthorizationHeader==null)
            return false;
        else{
            // TODO: check auth
        }

        return false;
    }
}
