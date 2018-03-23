package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.ServerNum;

import java.util.Map;

public class OicConfigImpl implements OicConfig {
    private OicConfigImpl() {}

    public static Builder oicConfigBuilder() {
        return new Builder();
    }

    public static Builder oicConfigBuilder(Map<String, String> properties) {
        return new Builder(properties);
    }

    public String buildUrlMaster(ServerNum serverNum) {
        return "jdbc:jtds:sqlserver://" + (serverNum == ServerNum.OIC01 ? oic01 : oic02) + ":" + port + ";user=" + user + ";" + "password=" + pass + ";databasename=" + masterDb;
    }

    public String buildUrlOIC(ServerNum serverType) {
        return "jdbc:jtds:sqlserver://" + (serverType == ServerNum.OIC01 ? oic01 : oic02) + ":" + port + ";user=" + user + ";" + "password=" + pass + ";databasename=" + oicDb;
    }

    private String oic01;
    private String oic02;
    private int port;
    private String user;
    private String pass;
    private String masterDb;
    private String oicDb;


    public static class Builder {
        private Builder() {}

        private Builder(Map<String, String> properties) {
            oic01 = properties.get("oic01");
            oic02 = properties.get("oic02");
            port = Integer.parseInt(properties.get("port"));
            user = properties.get("user");
            pass = properties.get("pass");
            masterDb = properties.get("masterDb");
            oicDb = properties.get("oicDb");
        }

        private String oic01;
        private String oic02;
        private int port;
        private String user;
        private String pass;
        private String masterDb;
        private String oicDb;

        public Builder oic01(String oic01) {
            this.oic01 = oic01;
            return this;
        }

        public Builder oic02(String oic02) {
            this.oic02 = oic02;
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

        public OicConfig build() {
            OicConfigImpl config = new OicConfigImpl();
            config.oic01 = this.oic01;
            config.oic02 = this.oic02;
            config.port = this.port;
            config.user = this.user;
            config.pass = this.pass;
            config.oicDb = this.oicDb;
            config.masterDb = this.masterDb;
            return config;
        }
    }
}
