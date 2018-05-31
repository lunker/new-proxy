package org.lunker.new_proxy.server;

import io.netty.channel.ChannelInitializer;
import org.lunker.new_proxy.sip.processor.PostProcessor;
import org.lunker.new_proxy.sip.processor.PreProcessor;
import org.lunker.new_proxy.sip.processor.ServerProcessor;

/**
 * Created by dongqlee on 2018. 4. 27..
 */
public abstract class TransportChannelInitializer extends ChannelInitializer{
//    protected ServerProcessor serverProcessor=null;
    protected PreProcessor preProcessor=null;

    // TODO: Refactoring - delete
    protected PostProcessor postProcessor=null;
    protected ServerProcessor serverProcessor=null;


    public TransportChannelInitializer() {
    }

    protected TransportChannelInitializer(ServerProcessor serverProcessor) {
        this.serverProcessor = serverProcessor;
    }
}
