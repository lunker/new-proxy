package proxy.sip.pre_process;

import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 5. 1..
 */
public class ProxyPreHandler {
    private Logger logger= LoggerFactory.getLogger(ProxyPreHandler.class);


    public ProxySipMessage handle(ProxySipMessage proxySipMessage){
        requestValidate();
        checkUri();

        return null;
    }

    public void requestValidate(){

    }


    private boolean checkUri(){
        return false;
    }

    private boolean checkMaxForwards(){
        return false;
    }

    private boolean checkProxyAuthorization(){
        return false;
    }
}
