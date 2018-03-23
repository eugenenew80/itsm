package kz.kegoc.bln.gateway.oic;

public interface OicConfig {
    String buildUrlMaster(Server serverType);
    String buildUrlOIC(Server serverType);
}
