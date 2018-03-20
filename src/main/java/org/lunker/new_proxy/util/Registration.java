package org.lunker.new_proxy.util;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class Registration {

    private String userKey;
    private String aor;
    private String account;
    private String domain;

    private Registration() {
    }

    public Registration(String userKey, String aor, String account, String domain) {
        this.userKey = userKey;
        this.aor = aor;
        this.account = account;
        this.domain = domain;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getAor() {
        return aor;
    }

    public void setAor(String aor) {
        this.aor = aor;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
