package org.lunker.new_proxy.util;

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

    private Registrar() {
        registrationMap=new HashMap<>(REGISTRAR_CAPACITY);
    }

    public static Registrar getInstance(){
        if (instance==null)
            instance=new Registrar();
        return instance;
    }

    public void register(String aor, Registration registration){
        synchronized (registrationMap){
            registrationMap.put(aor, registration);
        }
    }


    /*
    public Optional<Registration> get(String aor){
        return Optional.ofNullable(registrationMap.get(aor));

    }
*/

    public Registration get(String aor){
        return registrationMap.get(aor);
    }
}
