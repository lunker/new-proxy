package org.lunker.new_proxy.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lunker.new_proxy.core.constants.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class Configuration {

    private Logger logger= LoggerFactory.getLogger(Configuration.class);
    private static Configuration instance=null;
    private static JsonObject configurationJson=null;

    private ServerType serverType=ServerType.NONE;
    private Map<String, Object> tcpConfigMap=null;
    private Map<String, Object> udpConfigMap=null;


    private static final String TRANSPORT_HOST="host";
    private static final String TRANSPORT_PORT="port";

    private static final String TRANSPORT_TCP="tcp";
    private static final String TRANSPORT_UDP="udp";
    private static final String TRANSPORT_TLS="tls";
    private static final String TRANSPORT_HTTP="http";
    private static final String TRANSPORT_WS="ws";
    private static final String TRANSPORT_WSS="wss";

    boolean isValidServerType=false;
    boolean isValidTCP=false;
    boolean isValidUDP=false;
    boolean isValidTLS=false;
    boolean isValidHTTP=false;
    boolean isValidWS=false;
    boolean isValidWSS=false;


    /**
     * Initialize default transport options based on netty {@link io.netty.channel.ChannelOption} parameters
     */
    static {

    }

    public static Configuration getInstance() {
        if (instance==null)
            instance=new Configuration();
        return instance;
    }


    private Configuration() throws RuntimeException {

        // TCP Transport config
        tcpConfigMap=new HashMap<>();

        // UDP Transport config
        udpConfigMap=new HashMap<>();

        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("application.json").getFile());

        if(!file.exists())
            throw new RuntimeException("Server configuration file is not exist. Put 'application.json' under resources dir");
        else
            logger.debug("Find server configuration");


        try{
            String content = new String(Files.readAllBytes(file.toPath()));
            JsonParser jsonParser=new JsonParser();

            configurationJson=jsonParser.parse(content).getAsJsonObject();

            deserialize();
        }
        catch (Exception e){
//            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deserialize(){
        this.serverType=ServerType.convert(configurationJson.get("type").getAsString());
        if(this.serverType==ServerType.NONE)
            isValidServerType=false;

        JsonObject transportConfig=configurationJson.getAsJsonObject("transport");

        // TCP Server
        if(transportConfig.has(TRANSPORT_TCP)){
            JsonObject tcpJsonConfig=null;
            tcpJsonConfig=transportConfig.getAsJsonObject(TRANSPORT_TCP);

            if(validate(tcpJsonConfig)){
                setConfigMap(tcpConfigMap, tcpJsonConfig);
                isValidTCP=true;
            }
        }

        if(transportConfig.has(TRANSPORT_UDP)){

        }

        if(transportConfig.has(TRANSPORT_TLS)){

        }


        if(transportConfig.has(TRANSPORT_HTTP)){

        }

        if(transportConfig.has(TRANSPORT_WS)){

        }

        if(transportConfig.has(TRANSPORT_WSS)){

        }



    }

    private boolean validate(JsonObject config){
        if(config.has(TRANSPORT_HOST) && config.has(TRANSPORT_PORT))
            return true;
        else
            return false;
    }


    private void setConfigMap(Map<String, Object> configMap, JsonObject jsonConfig){
        Iterator<Map.Entry<String, JsonElement>> iterator=jsonConfig.entrySet().iterator();
        Map.Entry<String, JsonElement> entry=null;

        String key="";
        Object value=null;

        while(iterator.hasNext()){
            entry=iterator.next();

            try {
                key=entry.getKey();
                value=entry.getValue();

                value=((JsonElement) value).getAsInt();
                configMap.put(key, value);
            }
            catch (Exception e){
                value=((JsonElement) value).getAsString();
                configMap.put(key, value);
            }
        }
    }

    public JsonObject get(){
        return this.configurationJson;
    }


    public boolean isValidTCP() {
        return isValidTCP;
    }

    public boolean isValidUDP() {
        return isValidUDP;
    }

    public boolean isValidTLS() {
        return isValidTLS;
    }

    public boolean isValidHTTP() {
        return isValidHTTP;
    }

    public boolean isValidWS() {
        return isValidWS;
    }

    public boolean isValidWSS() {
        return isValidWSS;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public Map<String, Object> getTcpConfigMap() {
        return tcpConfigMap;
    }

    public Map<String, Object> getUdpConfigMap() {
        return udpConfigMap;
    }
}
