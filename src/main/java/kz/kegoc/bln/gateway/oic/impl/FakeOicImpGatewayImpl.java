package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.OicImpGateway;
import kz.kegoc.bln.gateway.oic.TelemetryRaw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Primary
public class FakeOicImpGatewayImpl implements OicImpGateway {
    private static final Logger logger = LoggerFactory.getLogger(FakeOicImpGatewayImpl.class);

    @Override
    public OicImpGateway config(OicConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public OicImpGateway points(List<Long> points) {
        this.points = points;
        return this;
    }

    @Override
    public List<TelemetryRaw> request(LocalDateTime requestedTime) throws Exception {
        logger.debug("FakeOicImpGatewayImpl.request started");

        List<TelemetryRaw> telemetryRawList = Arrays.asList(
            new TelemetryRaw(1l, 345d),
            new TelemetryRaw(2l, 456d)
        );

        logger.debug("FakeOicImpGatewayImpl.request completed");
        return telemetryRawList;
    }

    private OicConfig config;
    private List<Long> points;
}
