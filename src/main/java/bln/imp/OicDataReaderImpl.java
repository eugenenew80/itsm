package bln.imp;

import bln.entity.ArcType;
import bln.entity.LogPoint;
import bln.gateway.OicImpGateway;
import bln.gateway.OicImpGatewayBuilder;
import bln.gateway.TelemetryRaw;
import bln.repo.LogPointRepo;
import bln.web.dto.LogPointCfgDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static bln.gateway.impl.OicConfigImpl.oicConfigBuilder;

@Service
@RequiredArgsConstructor
public class OicDataReaderImpl implements OicDataReader {
    private final OicImpGatewayBuilder oicImpGatewayBuilder;
    private final LogPointRepo logPointRepo;

    @Resource(name="oicPropMap")
    private Map<String, String> oicProperty;

    @Override
    public List<TelemetryRaw> read(ArcType arcType, List<Long> points, LocalDateTime time) throws Exception {
        OicImpGateway oicImpGateway = oicImpGatewayBuilder
            .config(oicConfigBuilder(oicProperty).build())
            .points(points)
            .arcType(arcType)
            .build();

        return oicImpGateway.request(time);
    }

    @Override
    public void addPoints(List<LogPointCfgDto> points) {
        points.stream().forEach(point -> {
            LogPoint logPoint = logPointRepo.findOne(point.getLogPointId());
            if (logPoint==null) {
                logPoint = new LogPoint();
                logPoint.setId(point.getLogPointId());
                logPoint.setName("ТИ #" + point.getLogPointId());
                logPoint.setStartTime(point.getStart());
                logPoint.setIsNewPoint(true);
                logPointRepo.save(logPoint);
            }
        });
    }
}
