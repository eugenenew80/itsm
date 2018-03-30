package kz.kegoc.bln.schedule;

import kz.kegoc.bln.entity.*;
import kz.kegoc.bln.gateway.oic.*;
import kz.kegoc.bln.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static kz.kegoc.bln.gateway.oic.impl.OicConfigImpl.oicConfigBuilder;

@Component
public class ScheduledTasks implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final String DEF_ARC_TYPE_CODE = "MIN-3";
    private static final String DEF_ARC_TYPE_NAME = "Архив с шагом 3 минуты";
    private static final Long DEF_STEP = 180l;
    private boolean isReady = false;

    @Scheduled(cron = "30 */1 * * * *")
    public void startImport() {
        if (!isReady) return;

        logger.info("ScheduledTasks.startImport started");
        ArcType arcType = getDefArcType();
        try {
            readData(arcType);
        }
        catch (Exception e) {
            logger.error("ScheduledTasks.startImport crashed: " + e.getMessage());
        }
        logger.info("ScheduledTasks.startImport completed");
    }

    private void readData(ArcType arcType) throws Exception {
        Long step = arcType.getStep();

        LocalDateTime startTime = arcType.getLastLoadTime();
        LocalDateTime endTime = LocalDateTime.now()
            .truncatedTo(ChronoUnit.MINUTES);

        long sec = startTime.getMinute()*60 - Math.round(startTime.getMinute()*60 / step) * step;
        startTime = startTime.minusSeconds(sec).plusSeconds(step);

        sec = endTime.getMinute()*60 - Math.round(endTime.getMinute()*60 / step) * step;
        endTime = endTime.minusSeconds(sec);

        if (startTime.isAfter(endTime))
            return;

        logger.info("ArcType: " + arcType.getCode() + ", Period: " + startTime.toString() + " - " + endTime.toString());

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
        ArcType arcType = arcTypeRepo.findOne(DEF_ARC_TYPE_CODE);
        if (arcType==null) {
            arcType = new ArcType();
            arcType.setCode(DEF_ARC_TYPE_CODE);
            arcType.setName(DEF_ARC_TYPE_NAME);
            arcType.setStep(DEF_STEP);
            arcType.setLastLoadTime(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusSeconds(DEF_STEP));
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


    @Autowired
    private LogPointRepo logPointRepo;

    @Autowired
    private TelemetryRepo telemetryRepo;

    @Autowired
    private ArcTypeRepo arcTypeRepo;

    @Autowired
    private OicImpGatewayBuilder oicImpGatewayBuilder;

    @Resource(name="oicPropMap")
    private Map<String, String> oicProperty;
}
