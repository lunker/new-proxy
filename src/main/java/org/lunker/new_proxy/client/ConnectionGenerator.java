package org.lunker.new_proxy.client;

import org.lunker.new_proxy.exception.InvalidConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * interface to generate connection
 */
public interface ConnectionGenerator {
    public void generate(String host, int port, Class SipMessageHandlerImpl) throws Exception;
}
