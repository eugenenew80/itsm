package kz.kegoc.bln.gateway.oic;

import kz.kegoc.bln.gateway.oic.impl.OicConfigImpl;
import java.time.LocalDateTime;
import java.util.List;

public interface OicImpGateway {
    OicImpGateway config(OicConfigImpl config);
    OicImpGateway points(List<Long> points);
    List<TelemetryRaw> request(LocalDateTime requestedTime) throws Exception;
}
