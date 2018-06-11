package kz.kegoc.bln.gateway;

import java.time.LocalDateTime;
import java.util.List;

public interface OicImpGateway {
    List<TelemetryRaw> request(LocalDateTime dateTime) throws Exception;
}
