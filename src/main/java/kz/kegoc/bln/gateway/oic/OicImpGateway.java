package kz.kegoc.bln.gateway.oic;

import java.util.List;

public interface OicImpGateway {
    List<TelemetryRaw> request() throws Exception;
}
