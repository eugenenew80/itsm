package bln.gateway.impl;

import bln.gateway.OicConfig;
import bln.gateway.ServerNumEnum;
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
    public String urlMaster(ServerNumEnum serverNum) {
        return "jdbc:jtds:sqlserver://" + host(serverNum) + ":" + port + ";user=" + user + ";password=" + pass +  ";databasename=" + masterDb + ";loginTimeout=20";
    }

    @Override
    public String urlOic(ServerNumEnum serverNum) {
        return "jdbc:jtds:sqlserver://" + host(serverNum) + ":" + port + ";user=" + user + ";password=" + pass +  ";databasename=" + oicDb + ";loginTimeout=20";
    }

    private String host(ServerNumEnum serverNum) {
        return (serverNum == ServerNumEnum.OIC_01 ? oic01 : oic02);
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
