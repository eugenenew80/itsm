package kz.kegoc.bln.schedule;

import kz.kegoc.bln.entity.LastLoadInfo;
import kz.kegoc.bln.entity.LogPoint;
import kz.kegoc.bln.entity.Telemetry;
import kz.kegoc.bln.gateway.oic.OicImpGateway;
import kz.kegoc.bln.gateway.oic.TelemetryRaw;
import kz.kegoc.bln.repo.LastLoadInfoRepo;
import kz.kegoc.bln.repo.LogPointRepo;
import kz.kegoc.bln.repo.TelemetryRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import static kz.kegoc.bln.gateway.oic.OicConfig.propConfig;

@Component
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Scheduled(fixedRate = 60000)
    public void startImport() {
        logger.info("ScheduledTasks.startImport started");

        String defArcType = "SEC-5";
        Long defStep = 5l;

        LastLoadInfo lastLoadInfo = lastLoadInfoRepo.findOne(defArcType);
        if (lastLoadInfo==null) {
            lastLoadInfo = new LastLoadInfo();
            lastLoadInfo.setArcType(defArcType);
            lastLoadInfo.setStep(defStep);
            lastLoadInfo.setLastLoadTime(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
        }

        LocalDateTime curTime = lastLoadInfo.getLastLoadTime();
        LocalDateTime endTime = LocalDateTime.now()
            .minusMinutes(1)
            .truncatedTo(ChronoUnit.MINUTES);

        logger.info("start time: " + curTime.toString());
        logger.info("end time: " + endTime.toString());

        try {
            oicImpGateway
                .config(propConfig())
                .points(buildPoints());

            while (curTime.isBefore(endTime) || curTime.isEqual(endTime)) {
                List<TelemetryRaw> telemetryRawList = oicImpGateway.request(curTime);
                if (telemetryRawList.isEmpty()) {
                    logger.warn("No data at: " + curTime.toString());
                    continue;
                }

                save(curTime, telemetryRawList);
                lastLoadInfo.setLastLoadTime(curTime);
                lastLoadInfoRepo.save(lastLoadInfo);
                curTime = curTime.plusSeconds(defStep);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        logger.info("ScheduledTasks.startImport completed");
    }

    private List<Long> buildPoints() {
        return logPointRepo.findByIsActiveTrue()
            .stream()
            .map(t -> t.getId())
            .collect(Collectors.toList());
    }

    private void save(LocalDateTime dateTime, List<TelemetryRaw> telemetryRawList) {
        logger.info("ScheduledTasks.startImport save");
        List<Telemetry> list = telemetryRawList.stream()
            .map(t -> {
                Telemetry telemetry;
                List<Telemetry> existing = telemetryRepo.findByLogPointIdAndDateTime(t.getLogti(), dateTime);
                if (existing.isEmpty())
                    telemetry = new Telemetry();
                else
                    telemetry = existing.get(0);

                telemetry.setDateTime(dateTime);
                telemetry.setVal(t.getVal());
                telemetry.setLogPoint(new LogPoint(t.getLogti()));
                return telemetry;
            })
            .collect(Collectors.toList());

        telemetryRepo.save(list);
        logger.info("ScheduledTasks.startImport completed");
    }


    @Autowired
    private LogPointRepo logPointRepo;

    @Autowired
    private TelemetryRepo telemetryRepo;

    @Autowired
    private LastLoadInfoRepo lastLoadInfoRepo;

    @Autowired
    private OicImpGateway oicImpGateway;
}
