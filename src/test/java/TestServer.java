import org.junit.Test;
import org.lunker.new_proxy.Bootstrap;
import org.lunker.new_proxy.model.Transport;
import org.lunker.proxy.sip.SipServletImpl;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public class TestServer {
    @Test
    public void startServer() throws Exception{

        Bootstrap.addHandler(Transport.TCP, SipServletImpl.class);
        Bootstrap.addHandler(Transport.UDP, SipServletImpl.class);

        Bootstrap.addShutdownHandler();
        Bootstrap.run();
    }

    // message - > queue

    /*
    Queue -> SipMessageHandlerImpl 1
           -> SipMessageHandlerImpl 2
           -> SipMessageHandlerImpl3
           -> SipMessageHandlerImpl 4
     */


}

