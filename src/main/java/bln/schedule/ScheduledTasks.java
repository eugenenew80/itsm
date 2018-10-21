package bln.schedule;

import bln.entity.*;
import bln.gateway.TelemetryRaw;
import bln.imp.OicDataReader;
import bln.repo.*;
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
import java.util.Optional;
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

        List<ArcType> arcs = arcTypeRepo.findAll();
        for (ArcType arc : arcs) {
            if (!arc.getIsActive())
                continue;

            logger.info("arc type: " + arc.getCode());
            try {
                readArc(arc);
            }
            catch (Exception e) {
                logger.error("startImport crashed: " + e.getMessage());
            }
        }
        logger.info("startImport completed");
    }

    private void readArc(ArcType arcType) throws Exception {
        LocalDateTime startTime = arcType.getLastLoadTime();

        if (startTime == null && arcType.getCode().equals("MIN-60")) {
            startTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusDays(7);
            arcType.setLastLoadTime(startTime);
        }
        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        long sec = startTime.getMinute()*60 - Math.round(startTime.getMinute()*60 / arcType.getStep()) * arcType.getStep();
        startTime = startTime.minusSeconds(sec).plusSeconds(arcType.getStep());

        sec = endTime.getMinute()*60 - Math.round(endTime.getMinute()*60 / arcType.getStep()) * arcType.getStep();
        endTime = endTime.minusSeconds(sec);

        logger.info("Period: " + startTime.toString() + " - " + endTime.toString());
        if (startTime.isAfter(endTime))
            return;

        readDataNew(arcType, startTime);
        readData(arcType, startTime, endTime);
    }

    private void readData(ArcType arcType, LocalDateTime startTime, LocalDateTime endTime) throws Exception {
        LocalDateTime curTime = startTime;
        while (!curTime.isAfter(endTime)) {
            List<TelemetryRaw> telemetries = oicDataReader.read(arcType, buildPoints(false), curTime);
            save(arcType, curTime, telemetries);

            arcType.setLastLoadTime(curTime);
            arcTypeRepo.save(arcType);

            curTime = curTime.plusSeconds(arcType.getStep());
        }
    }

    private void readDataNew(ArcType arcType, LocalDateTime startTime) throws Exception {
        for (LogPoint point : logPointRepo.findAllByIsNewPoint(true)) {
            LocalDateTime curTime = Optional.ofNullable(point.getStartTime()).orElse(startTime);
            if (curTime.isBefore(startTime)) {
                while (!curTime.isAfter(startTime)) {
                    List<TelemetryRaw> telemetries = oicDataReader.read(arcType, Arrays.asList(point.getId()), curTime);
                    save(arcType, curTime, telemetries);
                    curTime = curTime.plusSeconds(arcType.getStep());
                }
                point.setLastLoadTime(curTime.minusSeconds(arcType.getStep()));
            }
            point.setIsNewPoint(false);
            logPointRepo.save(point);
        }
    }

    private List<Long> buildPoints(Boolean isNewPoint) {
        return logPointRepo.findAllByIsNewPoint(isNewPoint)
            .stream()
            .map(t -> t.getId())
            .collect(Collectors.toList());
    }

    private void save(ArcType arcType, LocalDateTime curTime, List<TelemetryRaw> telemetryRawList) {
        if (telemetryRawList.isEmpty()) {
            logger.warn("No data at: " + curTime.toString());
            MissingTelemetry missingTelemetry = new MissingTelemetry();
            missingTelemetry.setSystemCode("OIC");
            missingTelemetry.setArcTypeCode(arcType.getCode());
            missingTelemetry.setDateTime(curTime);
            missingTelemetryRepo.save(missingTelemetry);
            return;
        }

        List<Telemetry> list = telemetryRawList.stream()
            .map(t -> mapToTelemetry(arcType, curTime, t))
            .collect(Collectors.toList());

        telemetryRepo.save(list);
    }

    private Telemetry mapToTelemetry(ArcType arcType, LocalDateTime dateTime, TelemetryRaw t) {
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
