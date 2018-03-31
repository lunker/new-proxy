package org.lunker.new_proxy.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.lunker.new_proxy.sip.wrapper.message.GeneralSipMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 31..
 */
public class PreProcessActor extends AbstractActor {

    private Logger logger= LoggerFactory.getLogger(PreProcessActor.class);
    private GeneralSipMessage generalSipMessage=null;
    private ActorRef processActorRef=null;

    static public Props props(GeneralSipMessage generalSipMessage, ActorRef processActorRef) {
        return Props.create(PreProcessActor.class, () -> new PreProcessActor(generalSipMessage, processActorRef));
    }

    static public Props props( ActorRef processActorRef) {
        return Props.create(PreProcessActor.class, () -> new PreProcessActor(processActorRef));
    }

    public PreProcessActor(ActorRef processActorRef) {
        this.processActorRef = processActorRef;
    }

    public PreProcessActor() {
    }

    public PreProcessActor(GeneralSipMessage generalSipMessage, ActorRef processActorRef) {
        this.generalSipMessage = generalSipMessage;
        this.processActorRef = processActorRef;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GeneralSipMessage.class, (generalSipMessage)->{
                    logger.info("InPreProcessActor");

                    processActorRef.tell(generalSipMessage, getSelf());

                })
                .build();
    }
}
