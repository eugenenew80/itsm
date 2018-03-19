package kz.kegoc.bln.schedule;

import kz.kegoc.bln.gateway.oic.TelemetryRaw;
import kz.kegoc.bln.gateway.oic.impl.OicImpGatewayImpl;
import kz.kegoc.bln.repo.MeteringPointRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import static kz.kegoc.bln.gateway.oic.OicConfig.defaultConfig;
import static kz.kegoc.bln.util.Util.stream;

@Component
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Scheduled(fixedRate = 60000)
    public void startImport() {
        LocalDateTime requestedTime = LocalDateTime.parse("19.03.2018 05:05:00", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        try {
            List<TelemetryRaw> telemetry = new OicImpGatewayImpl()
                .config(defaultConfig())
                .points(buildPoints())
                .request(requestedTime);

            telemetry.stream().forEach(t -> logger.info(telemetry.toString()) );
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private List<Long> buildPoints() {
        return stream(meteringPointRepo.findAll())
            .map(t -> t.getId())
            .collect(Collectors.toList());
    }

    @Autowired
    private MeteringPointRepo meteringPointRepo;
}
