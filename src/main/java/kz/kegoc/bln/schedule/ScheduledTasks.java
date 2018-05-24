package kz.kegoc.bln.schedule;

import kz.kegoc.bln.entity.*;
import kz.kegoc.bln.gateway.oic.*;
import kz.kegoc.bln.repo.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static kz.kegoc.bln.gateway.oic.impl.OicConfigImpl.oicConfigBuilder;

@Component
@RequiredArgsConstructor
public class ScheduledTasks implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private final LogPointRepo logPointRepo;
    private final TelemetryRepo telemetryRepo;
    private final ArcTypeRepo arcTypeRepo;
    private final OicImpGatewayBuilder oicImpGatewayBuilder;

    @Resource(name="oicPropMap")
    private Map<String, String> oicProperty;

    @Value("${oic.arc.depth}")
    private Long defArcDepth;

    @Value("${oic.arc.code}")
    private String defArcCode;

    @Value("${oic.arc.name}")
    private String defArcName;

    @Value("${oic.arc.step}")
    private Long defArcStep;

    private boolean isReady = false;

    @Scheduled(cron = "30 */1 * * * *")
    public void startImport() {
        if (!isReady) return;

        logger.info("startImport started");

        List<ArcType> arcTypes = arcTypeRepo.findAll();
        if (arcTypes.isEmpty())
            arcTypes = Arrays.asList(getDefArcType());

        for (ArcType arcType : arcTypes) {
            if (!arcType.getIsActive())
                continue;

            logger.info("arc type: " + arcType.getCode());
            try {
                readData(arcType);
            }
            catch (Exception e) {
                logger.error("startImport crashed: " + e.getMessage());
            }
        }
        logger.info("startImport completed");
    }

    private void readData(ArcType arcType) throws Exception {
        LocalDateTime startTime = arcType.getLastLoadTime();

        Long step = arcType.getStep();
        LocalDateTime endTime = LocalDateTime.now()
            .truncatedTo(ChronoUnit.MINUTES);

        long sec = startTime.getMinute()*60 - Math.round(startTime.getMinute()*60 / step) * step;
        startTime = startTime.minusSeconds(sec).plusSeconds(step);

        sec = endTime.getMinute()*60 - Math.round(endTime.getMinute()*60 / step) * step;
        endTime = endTime.minusSeconds(sec);

        logger.info("Period: " + startTime.toString() + " - " + endTime.toString());
        if (startTime.isAfter(endTime))
            return;

        OicImpGateway oicImpGateway = oicImpGatewayBuilder
            .config(oicConfigBuilder(oicProperty).build())
            .points(buildPoints())
            .build();

        LocalDateTime curTime = startTime;
        while (!curTime.isAfter(endTime)) {
            List<TelemetryRaw> telemetries = oicImpGateway.request(curTime);
            save(arcType, curTime, telemetries);
            curTime = curTime.plusSeconds(step);
        }
    }

    private ArcType getDefArcType() {
        ArcType arcType = arcTypeRepo.findOne(defArcCode);
        if (arcType==null) {
            arcType = new ArcType();
            arcType.setCode(defArcCode);
            arcType.setName(defArcName);
            arcType.setStep(defArcStep);
            arcType.setLastLoadTime(
                    LocalDateTime.now()
                        .truncatedTo(ChronoUnit.HOURS)
                        .minusSeconds(defArcStep)
                        .minusDays(defArcDepth)
            );
        }

        return arcType;
    }

    private List<Long> buildPoints() {
        return logPointRepo.findByIsActiveTrue()
            .stream()
            .map(t -> t.getId())
            .collect(Collectors.toList());
    }

    private void save(ArcType arcType, LocalDateTime curTime, List<TelemetryRaw> telemetries) {
        if (telemetries.isEmpty()) {
            logger.warn("No data at: " + curTime.toString());
            return;
        }

        List<Telemetry> list = telemetries.stream()
            .map(t -> toTelemetry(arcType, curTime, t))
            .collect(Collectors.toList());
        telemetryRepo.save(list);

        arcType.setLastLoadTime(curTime);
        arcTypeRepo.save(arcType);
    }

    private Telemetry toTelemetry(ArcType arcType, LocalDateTime dateTime, TelemetryRaw t) {
        List<Telemetry> existing = telemetryRepo.findByLogPointIdAndDateTime(t.getLogti(), dateTime);
        Telemetry telemetry = existing.isEmpty() ? new Telemetry() : existing.get(0);
        telemetry.setLogPoint(new LogPoint(t.getLogti()));
        telemetry.setDateTime(dateTime);
        telemetry.setVal(t.getVal());
        telemetry.setSystemCode("OIC");
        telemetry.setArcTypeCode(arcType.getCode());
        return telemetry;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        isReady = true;
    }
}
