import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;
import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.exception.InvalidConfiguratoinException;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public class TestConfiguration {

    @Test
    public void loadConfig(){
        JsonObject config=Configuration.getInstance().get();
        Assert.assertEquals("proxy", config.get("type").getAsString());
    }

    @Test
    public void testValidate() throws InvalidConfiguratoinException{
        Configuration.getInstance().deserialize();
    }
}
