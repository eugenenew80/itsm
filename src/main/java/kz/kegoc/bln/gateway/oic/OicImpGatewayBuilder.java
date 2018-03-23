package kz.kegoc.bln.gateway.oic;

import java.time.LocalDateTime;
import java.util.List;

public interface OicImpGatewayBuilder {
    OicImpGatewayBuilder config(OicConfig config);
    OicImpGatewayBuilder points(List<Long> points);
    OicImpGatewayBuilder atDateTime(LocalDateTime atDateTime);
    OicImpGateway build() throws Exception;
}
