package kz.kegoc.bln.gateway.oic.impl.fake;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.OicImpGateway;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service("fakeOicImpGateway")
public class FakeOicImpGatewayBuilderImpl implements kz.kegoc.bln.gateway.oic.OicImpGatewayBuilder {
    @Override
    public FakeOicImpGatewayBuilderImpl config(OicConfig config) {
        return this;
    }

    @Override
    public FakeOicImpGatewayBuilderImpl points(List<Long> points) {
        return this;
    }

    @Override
    public FakeOicImpGatewayBuilderImpl atDateTime(LocalDateTime atDateTime) {
        return this;
    }

    @Override
    public OicImpGateway build() throws Exception {
        return new FakeOicImpGatewayImpl();
    }
}
