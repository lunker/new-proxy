package org.lunker.new_proxy.sip.processor;

import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.sip.processor.proxy.ProxyPreProcessor;
import org.lunker.new_proxy.stub.SipMessageHandler;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 27..
 */
public class ServerProcessor {
    private Transport transport=Transport.NONE;
    private PreProcessor preProcessor=null;
    private Optional<SipMessageHandler> sipMessageHandler=null;
    private PostProcessor postProcessor=null;

    private String sipMessageHandlerClassName="";

    public ServerProcessor() {
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
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


    //TODO: Refactoring
    public void setSipMessageHandlerClassName(String sipMessageHandlerClassName) throws ClassNotFoundException, IllegalAccessException, InstantiationException{
        this.sipMessageHandlerClassName = sipMessageHandlerClassName;

        this.sipMessageHandler=Optional.ofNullable((SipMessageHandler) Class.forName(this.sipMessageHandlerClassName).newInstance());
        this.preProcessor=new ProxyPreProcessor(this.sipMessageHandler);
    }

    public PostProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(PostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    public PreProcessor newPreProcessorInstance() {
        return new ProxyPreProcessor(this.sipMessageHandler);
    }

    public PreProcessor getPreProcessor() {
        return preProcessor;
    }
}
