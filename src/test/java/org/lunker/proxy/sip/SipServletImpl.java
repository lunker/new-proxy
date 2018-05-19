package org.lunker.proxy.sip;

import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.lunker.proxy.Validation;
import org.lunker.proxy.core.Message;
import org.lunker.proxy.sip.pre_process.ProxyPreHandler;
import org.lunker.proxy.sip.pro_process.ProxyPostHandler;
import org.lunker.proxy.sip.process.ProxyInHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 25..
 */
public class SipServletImpl extends SipMessageHandler {
    private Logger logger= LoggerFactory.getLogger(SipServletImpl.class);

    private ProxyPreHandler proxyPreHandler=null;
    private ProxyInHandler proxyInHandler=null;
    private ProxyPostHandler proxyProHandler=null;

    public SipServletImpl() {
        proxyPreHandler=new ProxyPreHandler();
        proxyInHandler=new ProxyInHandler();
        proxyProHandler=new ProxyPostHandler();
    }

    @Override
    public void handle(Optional<DefaultSipMessage> maybeDefaultSipMessage) {
        if(logger.isInfoEnabled())
            logger.info("[RECEIVED]:\n" + maybeDefaultSipMessage.get().toString());

        maybeDefaultSipMessage.ifPresent((defaultSipMessage)->{
            Message message=new Message(defaultSipMessage, new Validation());

            Mono<?> proxyAsync=Mono.just(message)
                    .map(proxyPreHandler::handle)
                    .map(proxyInHandler::handle)
                    .map(proxyProHandler::handle);

            proxyAsync=proxyAsync.subscribeOn(Schedulers.immediate());
            proxyAsync.subscribe();
        });
    }


}
