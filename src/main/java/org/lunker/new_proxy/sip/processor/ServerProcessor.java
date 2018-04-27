package org.lunker.new_proxy.sip.processor;

import org.lunker.new_proxy.stub.SipMessageHandler;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 27..
 */
public class ServerProcessor {
    private PreProcessor preProcessor=null;
    private Optional<SipMessageHandler> sipMessageHandler=null;
    private PostProcessor postProcessor=null;

    public ServerProcessor() {
    }

    public PreProcessor getPreProcessor() {
        return preProcessor;
    }

    public void setPreProcessor(PreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }

    public Optional<SipMessageHandler> getSipMessageHandler() {
        return sipMessageHandler;
    }

    public void setSipMessageHandler(SipMessageHandler sipMessageHandler) {
        this.sipMessageHandler = Optional.ofNullable(sipMessageHandler);
    }

    public void setSipMessageHandler(Optional<SipMessageHandler> sipMessageHandler) {
        this.sipMessageHandler = sipMessageHandler;
    }

    public PostProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(PostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }
}
