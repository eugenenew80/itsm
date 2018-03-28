package kz.kegoc.bln.gateway.oic;

import java.time.LocalDateTime;
import java.util.List;

public interface OicImpGateway {
    List<TelemetryRaw> request(LocalDateTime atDateTime) throws Exception;
}
