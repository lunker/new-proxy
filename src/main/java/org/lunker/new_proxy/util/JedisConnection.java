package org.lunker.new_proxy.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class JedisConnection {

    private static JedisConnection instance=null;

    private JedisPool jedisPool=null;
    private String redisHost="10.0.1.159";
    private int redisPort=6379;
    private int dbIndex=0;


    private JedisConnection() {
        jedisPool = new JedisPool(redisHost, redisPort);
    }

    public static JedisConnection getInstance(){
        if (instance==null){
            instance=new JedisConnection();
        }
        return instance;
    }

    public void set(String key, String value){
        Jedis jedis=null;

        try{
            jedis=jedisPool.getResource();

            jedis.set(key, value);

            jedis.close();
            jedis=null;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(jedis!=null)
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
