package kz.kegoc.bln.gateway.oic;

import kz.kegoc.bln.gateway.oic.impl.OicConfigImpl;
import java.io.InputStream;
import java.util.Properties;

public interface OicConfig {
    String buildUrlMaster(Server serverType);

    String buildUrlOIC(Server serverType);

    static OicConfig defaultConfig() {
        return OicConfigImpl.builder()
            .serverOne("OIC01UG.CORP.KEGOC.KZ")
            .serverTwo("OIC02UG.CORP.KEGOC.KZ")
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
                .serverOne(props.getProperty("server1"))
                .serverTwo(props.getProperty("server2"))
                .port(Integer.parseInt(props.getProperty("port")))
                .user(props.getProperty("user"))
                .pass(props.getProperty("pass"))
                .masterDb(props.getProperty("masterDb"))
                .oicDb(props.getProperty("oicDb"))
                .build();
        }
    }
}
