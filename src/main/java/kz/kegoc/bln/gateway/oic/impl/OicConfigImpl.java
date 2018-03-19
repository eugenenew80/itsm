package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.ServerType;

public class OicConfigImpl implements OicConfig {
    private OicConfigImpl() {}

    public static Builder builder() {
        return new Builder();
    }

    public String buildUrlMaster(ServerType serverType) {
        return "jdbc:sqlserver://" + (serverType == ServerType.ServerOne ? server1 : server2) + ":" + port + ";user=" + user + ";" + "password=" + pass + ";databasename=" + masterDb;
    }

    public String buildUrlOIC(ServerType serverType) {
        return "jdbc:sqlserver://" + (serverType == ServerType.ServerOne ? server1 : server2) + ":" + port + ";user=" + user + ";" + "password=" + pass + ";databasename=" + oicDb;
    }

    private String server1;
    private String server2;
    private int port;
    private String user;
    private String pass;
    private String masterDb;
    private String oicDb;


    public static class Builder {
        private Builder() {}

        private String server1;
        private String server2;
        private int port;
        private String user;
        private String pass;
        private String masterDb;
        private String oicDb;

        public Builder server1(String server1) {
            this.server1 = server1;
            return this;
        }

        public Builder server2(String server2) {
            this.server2 = server2;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder pass(String pass) {
            this.pass = pass;
            return this;
        }

        public Builder masterDb(String masterDb) {
            this.masterDb = masterDb;
            return this;
        }

        public Builder oicDb(String oicDb) {
            this.oicDb = oicDb;
            return this;
        }

        public OicConfigImpl build() {
            OicConfigImpl config = new OicConfigImpl();
            config.server1 = this.server1;
            config.server2 = this.server2;
            config.port = this.port;
            config.user = this.user;
            config.pass = this.pass;
            config.oicDb = this.oicDb;
            config.masterDb = this.masterDb;
            return config;
        }
    }
}
