package kz.kegoc.bln.gateway.oic;

import java.util.List;

public interface OicImpGatewayBuilder {
    OicImpGatewayBuilder config(OicConfig config);
    OicImpGatewayBuilder points(List<Long> points);
    OicImpGateway build() throws Exception;
}
