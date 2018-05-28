package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.entity.ArcType;
import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.OicImpGateway;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
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
    public OicImpGatewayBuilderImpl arcType(ArcType arcType) {
        this.arcType = arcType;
        return this;
    }

    @Override
    public OicImpGateway build() throws Exception {
        validate();
        return new OicImpGatewayImpl(new OicDatabaseImpl(config), points, arcType);
    }

    private void validate() throws Exception {
        if (points==null || points.isEmpty())
            throw new Exception("points must be specified");

        if (config==null)
            throw new Exception("config must be specified");
    }

    private OicConfig config;
    private List<Long> points;
    private ArcType arcType;
}
