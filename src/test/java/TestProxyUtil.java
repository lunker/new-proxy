import org.junit.Test;
import org.lunker.new_proxy.util.ProxyUtil;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class TestProxyUtil {

    @Test
    public void testGenerateRandStr(){

        String result=ProxyUtil.generateRandStr(50);

        System.out.println(result);
    }
}
