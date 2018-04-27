import org.junit.Test;
import org.lunker.new_proxy.Bootstrap;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipMessage;
import org.lunker.new_proxy.stub.SipMessageHandler;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public class TestServer {

    @Test
    public void startServer(){
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.start("tcp", new SipMessageHandlerImpl());
    }
}


class SipMessageHandlerImpl implements SipMessageHandler {
    @Override
    public void handle(Optional<ProxySipMessage> generalSipMessage) {
        System.out.println("!@#");
    }
}
