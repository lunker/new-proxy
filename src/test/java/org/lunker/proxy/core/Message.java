package org.lunker.proxy.core;

import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;
import org.lunker.proxy.Validation;

/**
 * Created by dongqlee on 2018. 5. 17..
 */
public class Message {
    private ProcessState processState;
    private DefaultSipMessage originalMessage;
    private DefaultSipMessage newMessage;
    private Validation validation;

    public Message() {
    }

    public Message(DefaultSipMessage originalMessage, Validation validation) {
        this.processState= ProcessState.PRE;
        this.originalMessage = originalMessage;
        this.newMessage = null;
        this.validation = validation;
    }

    public Validation getValidation() {
        return validation;
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public DefaultSipMessage getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(DefaultSipMessage originalMessage) {
        this.originalMessage = originalMessage;
    }

    public DefaultSipMessage getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(DefaultSipMessage newMessage) {
        this.newMessage = newMessage;
    }

    @Override
    public String toString() {
        return "Message{" +
                "processState=" + processState +
                ", originalMessage=" + originalMessage +
                ", newMessage=" + newMessage +
                ", validation=" + validation +
                '}';
    }
}
