package kz.kegoc.bln.gateway.oic;

import kz.kegoc.bln.gateway.oic.impl.OicConfigImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public interface OicConfig {
    String buildUrlMaster(ServerType serverType);

    String buildUrlOIC(ServerType serverType);

    static OicConfig defaultConfig() {
        return OicConfigImpl.builder()
            .server1("OIC01UG.CORP.KEGOC.KZ")
            .server2("OIC02UG.CORP.KEGOC.KZ")
            .port(1433)
            .user("bln")
            .pass("123456")
            .masterDb("MASTER")
            .oicDb("OICDB")
            .build();
    }

    static OicConfig propConfig() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try(InputStream resourceStream = loader.getResourceAsStream("oic.properties")) {
            props.load(resourceStream);
            return OicConfigImpl.builder()
                .server1(props.getProperty("server1"))
                .server2(props.getProperty("server2"))
                .port(Integer.parseInt(props.getProperty("port")))
                .user(props.getProperty("user"))
                .pass(props.getProperty("pass"))
                .masterDb(props.getProperty("masterDb"))
                .oicDb(props.getProperty("oicDb"))
                .build();
        }
    }
}
