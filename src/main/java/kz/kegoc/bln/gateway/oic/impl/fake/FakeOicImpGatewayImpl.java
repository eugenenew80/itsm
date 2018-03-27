package kz.kegoc.bln.gateway.oic.impl.fake;

import kz.kegoc.bln.gateway.oic.OicImpGateway;
import kz.kegoc.bln.gateway.oic.TelemetryRaw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class FakeOicImpGatewayImpl implements OicImpGateway {
    private static final Logger logger = LoggerFactory.getLogger(FakeOicImpGatewayImpl.class);

    @Override
    public List<TelemetryRaw> request() throws Exception {
        logger.debug("FakeOicImpGatewayImpl.request started");

        List<TelemetryRaw> telemetryRawList = Arrays.asList(
            new TelemetryRaw(1l, 345d),
            new TelemetryRaw(2l, 456d)
        );

        logger.debug("FakeOicImpGatewayImpl.request completed");
        return telemetryRawList;
    }

    @Override
    public List<TelemetryRaw> request(LocalDateTime atDateTime) throws Exception {
        return request();
    }
}
