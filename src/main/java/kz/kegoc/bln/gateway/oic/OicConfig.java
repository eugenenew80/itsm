package kz.kegoc.bln.gateway.oic;

import kz.kegoc.bln.gateway.oic.impl.OicConfigImpl;

import java.util.Arrays;
import java.util.List;

public interface OicConfig {
    String buildUrlMaster(ServerType serverType);
    String buildUrlOIC(ServerType serverType);

    static OicConfigImpl defaultConfig() {
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

    static List<Long> defaultPoints() {
        return Arrays.asList(1L, 2L);
    }
}
