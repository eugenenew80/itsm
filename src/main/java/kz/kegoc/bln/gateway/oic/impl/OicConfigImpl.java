package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.ServerNum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class OicConfigImpl implements OicConfig {
    private static final Logger logger = LoggerFactory.getLogger(OicConfigImpl.class);

    private OicConfigImpl() {}

    public static Builder oicConfigBuilder(Map<String, String> properties) {
        return new Builder(properties);
    }

    @Override
    public String urlMaster(ServerNum serverNum) {
        return "jdbc:jtds:sqlserver://" + host(serverNum) + ":" + port + ";user=" + user + ";password=" + pass +  ";databasename=" + masterDb;
    }

    @Override
    public String urlOic(ServerNum serverNum) {
        return "jdbc:jtds:sqlserver://" + host(serverNum) + ":" + port + ";user=" + user + ";password=" + pass +  ";databasename=" + oicDb;
    }

    private String host(ServerNum serverNum) {
        return (serverNum == ServerNum.OIC_01 ? oic01 : oic02);
    }

    private String oic01;
    private String oic02;
    private int port;
    private String user;
    private String pass;
    private String masterDb;
    private String oicDb;


    public static class Builder {
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
