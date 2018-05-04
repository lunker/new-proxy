import org.junit.Test;
import org.lunker.new_proxy.Bootstrap;
import proxy.core.SipServletImpl;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public class TestServer {

    @Test
    public void startServer() throws Exception{
        Bootstrap bootstrap=new Bootstrap();

        bootstrap.start("tcp", SipServletImpl.class);
    }

    // message - > queue

    /*
    Queue -> SipMessageHandlerImpl 1
           -> SipMessageHandlerImpl 2
           -> SipMessageHandlerImpl3
           -> SipMessageHandlerImpl 4
     */
}

