package org.lunker.new_proxy.util;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class Registrar {

    public static Registrar instance=null;

    private int REGISTRAR_CAPACITY=1000;
    // key: aor
    // value: Registration info
    private Map<String, Registration> registrationMap;
    private Map<String, ChannelHandlerContext> ctxMap;

    private Registrar() {
        registrationMap=new HashMap<>(REGISTRAR_CAPACITY);
        ctxMap=new HashMap<>(REGISTRAR_CAPACITY);
    }

    public static Registrar getInstance(){
        if (instance==null)
            instance=new Registrar();
        return instance;
    }

    public void register(String userKey, Registration registration, ChannelHandlerContext ctx){
        synchronized (registrationMap){
            registrationMap.put(userKey, registration);
            ctxMap.put(userKey, ctx);
        }
    }

    /*
    public Optional<Registration> get(String aor){
        return Optional.ofNullable(registrationMap.get(aor));
    }
    */

    public Registration getRegistration(String userKey){
        return registrationMap.get(userKey);
    }

    public ChannelHandlerContext getCtx(String userKey){
        synchronized (this.ctxMap){
            return ctxMap.get(userKey);
        }
    }


}
