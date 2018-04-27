package org.lunker.new_proxy.server;

import io.netty.channel.ChannelInitializer;
import org.lunker.new_proxy.sip.processor.PostProcessor;
import org.lunker.new_proxy.sip.processor.PreProcessor;

/**
 * Created by dongqlee on 2018. 4. 27..
 */
public abstract class TransportInitializer extends ChannelInitializer{
//    protected ServerProcessor serverProcessor=null;
    protected PreProcessor preProcessor=null;
    protected PostProcessor postProcessor=null;
}
