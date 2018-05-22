package kz.kegoc.bln.gateway.oic;

public interface OicConfig {
    String urlMaster(ServerNumEnum serverNum);
    String urlOic(ServerNumEnum serverNum);
}
