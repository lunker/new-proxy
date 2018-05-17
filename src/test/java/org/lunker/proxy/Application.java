package org.lunker.proxy;

import org.lunker.new_proxy.Bootstrap;
import org.lunker.proxy.sip.SipServletImpl;

/**
 * Created by dongqlee on 2018. 5. 9..
 */
public class Application {
    public static void main(String[] args){
        try{
            Bootstrap.start("tcp", SipServletImpl.class);

        }
        catch (Exception e ){
            e.printStackTrace();
        }
    }
}
