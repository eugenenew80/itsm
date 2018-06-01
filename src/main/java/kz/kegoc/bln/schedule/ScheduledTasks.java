package kz.kegoc.bln.schedule;

import kz.kegoc.bln.entity.*;
import kz.kegoc.bln.gateway.oic.*;
import kz.kegoc.bln.imp.OicDataReader;
import kz.kegoc.bln.repo.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduledTasks implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final LogPointRepo logPointRepo;
    private final TelemetryRepo telemetryRepo;
    private final MissingTelemetryRepo missingTelemetryRepo;
    private final ArcTypeRepo arcTypeRepo;
    private final OicDataReader oicDataReader;
    private boolean isReady = false;

    @Scheduled(cron = "30 */1 * * * *")
    public void startImport() {
        if (!isReady) return;
        logger.info("startImport started");

        List<ArcType> arcTypes = arcTypeRepo.findAll();
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

        if (startTime==null && arcType.getCode().equals("MIN-60")) {
            startTime = LocalDateTime.now()
                .truncatedTo(ChronoUnit.HOURS)
                .minusDays(7);
            arcType.setLastLoadTime(startTime);
        }

        LocalDateTime endTime = LocalDateTime.now()
            .truncatedTo(ChronoUnit.MINUTES);

        long sec = startTime.getMinute()*60 - Math.round(startTime.getMinute()*60 / arcType.getStep()) * arcType.getStep();
        startTime = startTime.minusSeconds(sec).plusSeconds(arcType.getStep());

        sec = endTime.getMinute()*60 - Math.round(endTime.getMinute()*60 / arcType.getStep()) * arcType.getStep();
        endTime = endTime.minusSeconds(sec);


        for (LogPoint point : logPointRepo.findAllByIsNewPoint(true)) {
            LocalDateTime curTime = point.getStartTime();
            while (!curTime.isAfter(arcType.getLastLoadTime())) {
                List<TelemetryRaw> telemetries = oicDataReader.read(arcType, Arrays.asList(point.getId()), curTime);
                save(arcType, curTime, telemetries);
                curTime = curTime.plusSeconds(arcType.getStep());
            }
            point.setIsNewPoint(false);
            point.setLastLoadTime(curTime.minusSeconds(arcType.getStep()));
            logPointRepo.save(point);
        }

        logger.info("Period: " + startTime.toString() + " - " + endTime.toString());
        if (startTime.isAfter(endTime))
            return;

        LocalDateTime curTime = startTime;
        while (!curTime.isAfter(endTime)) {
            List<TelemetryRaw> telemetries = oicDataReader.read(arcType, buildPoints(false), curTime);
            save(arcType, curTime, telemetries);

            arcType.setLastLoadTime(curTime);
            arcTypeRepo.save(arcType);

            saveMissing(curTime, telemetries);
            curTime = curTime.plusSeconds(arcType.getStep());
        }
    }

    private List<Long> buildPoints(Boolean isNewPoint) {
        return logPointRepo.findAllByIsNewPoint(isNewPoint)
            .stream()
            .map(t -> t.getId())
            .collect(Collectors.toList());
    }

    private void save(ArcType arcType, LocalDateTime curTime, List<TelemetryRaw> telemetries) {
        if (telemetries.isEmpty()) {
            logger.warn("No data at: " + curTime.toString());
            MissingTelemetry missingTelemetry = new MissingTelemetry();
            missingTelemetry.setSystemCode("OIC");
            missingTelemetry.setArcTypeCode(arcType.getCode());
            missingTelemetry.setDateTime(curTime);
            missingTelemetryRepo.save(missingTelemetry);
            return;
        }

        List<Telemetry> list = telemetries.stream()
            .map(t -> toTelemetry(arcType, curTime, t))
            .collect(Collectors.toList());

        telemetryRepo.save(list);
    }

    private void saveMissing(LocalDateTime curTime, List<TelemetryRaw> telemetries) {
        List<LogPoint> points = telemetries.stream().map(t -> {
            LogPoint logPoint = logPointRepo.findOne(t.getLogti());
            if (logPoint != null)
                logPoint.setLastLoadTime(curTime);
            return logPoint;
        })
        .filter(l -> l != null)
        .collect(Collectors.toList());
        logPointRepo.save(points);
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
