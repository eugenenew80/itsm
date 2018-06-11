package kz.kegoc.bln.gateway;

public interface OicConfig {
    String urlMaster(ServerNumEnum serverNum);
    String urlOic(ServerNumEnum serverNum);
}
