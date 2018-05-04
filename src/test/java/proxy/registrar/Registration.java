package proxy.registrar;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class Registration {

    private String userKey;
    private String aor;
    private String account;
    private String domain;

    private String remoteAddress;
    private int remotePort;

    private Registration() {
    }

    public Registration(String userKey, String aor, String account, String domain) {
        this.userKey = userKey;
        this.aor = aor;
        this.account = account;
        this.domain = domain;
    }

    public Registration(String userKey, String aor, String account, String domain, String remoteAddress, int remotePort) {
        this.userKey = userKey;
        this.aor = aor;
        this.account = account;
        this.domain = domain;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
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

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
}
