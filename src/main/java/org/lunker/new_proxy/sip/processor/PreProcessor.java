package org.lunker.new_proxy.sip.processor;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
@ChannelHandler.Sharable
public abstract class PreProcessor extends ChannelInboundHandlerAdapter {
}
