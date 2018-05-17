package org.lunker.proxy.sip.pro_process;

import org.lunker.proxy.core.Message;
import org.lunker.proxy.core.ProcessState;
import org.lunker.proxy.core.ProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 5. 1..
 */
public class ProxyPostHandler implements ProxyHandler {
    private Logger logger= LoggerFactory.getLogger(ProxyPostHandler.class);

    @Override
    public Message handle(Message message) {

        // TODO:
        if(message.getProcessState() != ProcessState.POST)
            return message;

        try{
            message.getNewMessage().send();

            if(logger.isInfoEnabled())
                logger.info("[SENT]\n{}", message.getNewMessage());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return message;
    }
}
