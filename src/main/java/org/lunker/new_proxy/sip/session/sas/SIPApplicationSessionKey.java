package org.lunker.new_proxy.sip.session.sas;

import org.lunker.new_proxy.util.HashUtil;

import java.util.UUID;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class SIPApplicationSessionKey {

    private String uuid;
    private String generatedKey="";
    private int MAX_HASHED_LENGTH=8;

    public SIPApplicationSessionKey() {
        this.uuid=""+UUID.randomUUID();
        generateKey();
    }

    public SIPApplicationSessionKey(String generatedKey) {
        this.generatedKey = generatedKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            SIPApplicationSessionKey other=(SIPApplicationSessionKey)obj;

            if(!other.getGeneratedKey().equals(this.getGeneratedKey())){
                return false;
            }
            else
                return true;
        }
    }

    private void generateKey(){
        this.generatedKey= HashUtil.hashString(this.uuid, MAX_HASHED_LENGTH);// hash
    }

    public String getGeneratedKey() {
//        if(this.generatedKey.equals(""))
//            this.generateKey();
        return this.generatedKey;
    }

    @Override
    public String toString() {
        return this.generatedKey;
    }
}
