package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.Server;

import java.util.Map;

public class OicConfigImpl implements OicConfig {
    private OicConfigImpl() {}

    public static Builder oicConfigBuilder() {
        return new Builder();
    }

    public static Builder oicConfigBuilder(Map<String, String> properties) {
        return new Builder(properties);
    }

    public String buildUrlMaster(Server serverType) {
        return "jdbc:jtds:sqlserver://" + (serverType == Server.ServerOne ? serverOne : serverTwo) + ":" + port + ";user=" + user + ";" + "password=" + pass + ";databasename=" + masterDb;
    }

    public String buildUrlOIC(Server serverType) {
        return "jdbc:jtds:sqlserver://" + (serverType == Server.ServerOne ? serverOne : serverTwo) + ":" + port + ";user=" + user + ";" + "password=" + pass + ";databasename=" + oicDb;
    }

    private String serverOne;
    private String serverTwo;
    private int port;
    private String user;
    private String pass;
    private String masterDb;
    private String oicDb;


    public static class Builder {
        private Builder() {}

        private Builder(Map<String, String> properties) {
            serverOne = properties.get("serverOne");
            serverTwo = properties.get("serverTwo");
            port = Integer.parseInt(properties.get("port"));
            user = properties.get("user");
            pass = properties.get("pass");
            masterDb = properties.get("masterDb");
            oicDb = properties.get("oicDb");
        }

        private String serverOne;
        private String serverTwo;
        private int port;
        private String user;
        private String pass;
        private String masterDb;
        private String oicDb;

        public Builder serverOne(String serverOne) {
            this.serverOne = serverOne;
            return this;
        }

        public Builder serverTwo(String serverTwo) {
            this.serverTwo = serverTwo;
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
            config.serverOne = this.serverOne;
            config.serverTwo = this.serverTwo;
            config.port = this.port;
            config.user = this.user;
            config.pass = this.pass;
            config.oicDb = this.oicDb;
            config.masterDb = this.masterDb;
            return config;
        }
    }
}
