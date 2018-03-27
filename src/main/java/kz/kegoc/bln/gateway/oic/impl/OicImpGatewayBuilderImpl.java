package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.OicImpGateway;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service("defOicImpGateway")
@Primary
public class OicImpGatewayBuilderImpl implements kz.kegoc.bln.gateway.oic.OicImpGatewayBuilder {

    @Override
    public OicImpGatewayBuilderImpl config(OicConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public OicImpGatewayBuilderImpl points(List<Long> points) {
        this.points = points;
        return this;
    }

    @Override
    public OicImpGatewayBuilderImpl atDateTime(LocalDateTime atDateTime) {
        this.atDateTime = atDateTime;
        return this;
    }

    @Override
    public OicImpGateway build() throws Exception {
        validate();
        return new OicImpGatewayImpl(config, points, atDateTime);
    }

    private void validate() throws Exception {
        if (atDateTime ==null)
            throw new Exception("requestedTime  must be specified");

        if (points==null || points.isEmpty())
            throw new Exception("points must be specified");

        if (config==null)
            throw new Exception("config  must be specified");
    }

    private OicConfig config;
    private List<Long> points;
    private LocalDateTime atDateTime;
}
