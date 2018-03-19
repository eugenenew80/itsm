package kz.kegoc.bln.gateway.oic;


import java.time.LocalDateTime;
import java.util.List;

public interface OicDataGateway {
    OicDataGateway config(OicConfig config);

    OicDataGateway points(List<Long> points);

    List<TelemetryRaw> request(LocalDateTime requestedTime) throws Exception;
}
