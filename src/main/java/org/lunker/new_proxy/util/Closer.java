package org.lunker.new_proxy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class Closer {
    private static Logger logger= LoggerFactory.getLogger(Closer.class);

    public static void graceFullyShutdown(){
        logger.info("Gracefully shutdown proxy");
        JedisConnection.getInstance().close();
    }
}
