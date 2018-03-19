package org.lunker.new_proxy.util;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.Serializable;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class JedisConnection {

    private static JedisConnection instance=null;

    private Gson gson=null;
    private JedisPool jedisPool=null;
    private String redisHost="10.0.1.159";
    private int redisPort=6379;
    private int dbIndex=0;


    private JedisConnection() {
        jedisPool = new JedisPool(redisHost, redisPort);
        gson = new Gson();
    }

    public static JedisConnection getInstance(){
        if (instance==null){
            instance=new JedisConnection();
        }
        return instance;
    }

    public <T extends Serializable> void set(String key, T value){
        Jedis jedis=null;

        try{
            jedis=jedisPool.getResource();

            String serializedValue=gson.toJson(value);

            jedis.set(key, serializedValue);

            jedis.close();
            jedis=null;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(jedis==null)
                jedis.close();
        }
    }

    public void close(){
        try{
            if(jedisPool!=null)
                jedisPool.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
