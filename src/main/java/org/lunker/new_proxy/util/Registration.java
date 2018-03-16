package org.lunker.new_proxy.util;

import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.exception.ParameterInsufficient;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class Registration {

    private ChannelHandlerContext ctx;
    private String aor;
    private String account;
    private String domain;

    private Registration() {
    }

    public Registration(ChannelHandlerContext ctx, String aor, String account, String domain) {
        this.ctx = ctx;
        this.aor = aor;
        this.account = account;
        this.domain = domain;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
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


    public class Builder{

        private ChannelHandlerContext ctx=null;
        private String aor="";
        private String account="";
        private String domain="";

        public Builder(ChannelHandlerContext ctx) {
            this.ctx=ctx;
        }

        public Builder setAor(String aor){
            this.aor=aor;
            return this;
        }

        public Builder setAccount(String account){
            this.account=account;
            return this;
        }

        public Builder setDomain(String domain){
            this.domain=domain;
            return this;
        }

        public Registration build() throws ParameterInsufficient{
            if(ctx==null || aor.equals("") || account.equals("") || domain.equals("")){
                throw new ParameterInsufficient();
            }
            return new Registration(this.ctx, this.aor, this.account, this.domain);
        }
    }
}
