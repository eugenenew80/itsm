package kz.kegoc.bln.gateway.oic;

public interface OicConfig {
    String buildUrlMaster(ServerNum serverNum);
    String buildUrlOIC(ServerNum serverNum);
}
