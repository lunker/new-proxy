package org.lunker.new_proxy.akka;

import akka.actor.AbstractActor;
import akka.actor.Props;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 31..
 */
public class PostProcessActor extends AbstractActor{

    private Logger logger= LoggerFactory.getLogger(PostProcessActor.class);
    static public Props props() {
        return Props.create(PostProcessActor.class, () -> new PostProcessActor());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(GeneralSipMessage.class, (generalSipMessage)->{
            logger.info("In PostprocessActor");
            generalSipMessage.send();

//            logger.info("[SENT]: \n" + generalSipMessage.toString());
        }).build();
    }
}
