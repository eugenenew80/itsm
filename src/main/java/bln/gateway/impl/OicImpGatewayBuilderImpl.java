package bln.gateway.impl;

import bln.entity.ArcType;
import bln.gateway.OicConfig;
import bln.gateway.OicImpGateway;
import bln.gateway.OicImpGatewayBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Primary
public class OicImpGatewayBuilderImpl implements OicImpGatewayBuilder {

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
