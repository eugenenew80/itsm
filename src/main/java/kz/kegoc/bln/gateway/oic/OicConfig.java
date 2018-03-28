package kz.kegoc.bln.gateway.oic;


public interface OicConfig {
    String urlMaster(ServerNum serverNum);
    String urlOic(ServerNum serverNum);
}
