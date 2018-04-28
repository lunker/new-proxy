package org.lunker.new_proxy.sip.wrapper.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public abstract class AbstractSipMessage implements SipMessage{
    private Logger logger= LoggerFactory.getLogger(AbstractSipMessage.class);


    @Override
    public void addHeader() {

    }

    @Override
    public void getHeader() {

    }
}
