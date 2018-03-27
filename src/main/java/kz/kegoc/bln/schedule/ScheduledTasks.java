package kz.kegoc.bln.schedule;

import kz.kegoc.bln.entity.*;
import kz.kegoc.bln.gateway.oic.*;
import kz.kegoc.bln.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Scheduled(fixedRate = 60000)
    public void startImport() {
        logger.info("ScheduledTasks.startImport started");

        LastLoadInfo lastLoadInfo = buildLastLoadInfo();
        LocalDateTime curTime = lastLoadInfo.getLastLoadTime();
        LocalDateTime endTime = LocalDateTime.now()
            .minusMinutes(1)
            .truncatedTo(ChronoUnit.MINUTES);

        logger.info("Period: " + curTime.toString() + " - " + endTime.toString());
        try {
            while (curTime.isBefore(endTime) || curTime.isEqual(endTime)) {
                importAtTime(lastLoadInfo, curTime);
                curTime = curTime.plusSeconds(lastLoadInfo.getStep());
            }
        }
        catch (Exception e) {
            logger.error("ScheduledTasks.startImport terminated: " + e.getMessage());
        }

        logger.info("ScheduledTasks.startImport completed");
    }

    private LastLoadInfo buildLastLoadInfo() {
        String defArcType = "SEC-5";
        Long defStep = 5l;

        LastLoadInfo lastLoadInfo = lastLoadInfoRepo.findOne(defArcType);
        if (lastLoadInfo==null) {
            lastLoadInfo = new LastLoadInfo();
            lastLoadInfo.setArcType(defArcType);
            lastLoadInfo.setStep(defStep);
            lastLoadInfo.setLastLoadTime(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
        }

        return lastLoadInfo;
    }

    private void importAtTime(LastLoadInfo lastLoadInfo, LocalDateTime curTime) throws Exception {
        logger.info("Request data from OIC at time: " + curTime.toString());
        List<TelemetryRaw> telemetries = oicImpGatewayBuilder
            .config(oicConfigBuilder(oicProperty).build())
            .points(buildPoints())
            .atDateTime(curTime)
            .build()
            .request();
        logger.info("Request data completed, record count: " + telemetries.size());

        if (telemetries.isEmpty()) {
            logger.warn("No data at: " + curTime.toString());
            return;
        }

        logger.info("Save data started at time: "+ curTime.toString());
        save(curTime, telemetries);
        lastLoadInfo.setLastLoadTime(curTime);
        lastLoadInfoRepo.save(lastLoadInfo);
        logger.info("Save data completed");
    }

    private List<Long> buildPoints() {
        return logPointRepo.findByIsActiveTrue()
            .stream()
            .map(t -> t.getId())
            .collect(Collectors.toList());
    }

    private void save(LocalDateTime dateTime, List<TelemetryRaw> telemetryRawList) {
        logger.debug("ScheduledTasks.startImport save");
        List<Telemetry> list = telemetryRawList.stream()
            .map(t -> toTelemetry(dateTime, t))
            .collect(Collectors.toList());

        telemetryRepo.save(list);
        logger.debug("ScheduledTasks.startImport completed");
    }

    private Telemetry toTelemetry(LocalDateTime dateTime, TelemetryRaw t) {
        List<Telemetry> existing = telemetryRepo.findByLogPointIdAndDateTime(t.getLogti(), dateTime);
        Telemetry telemetry = existing.isEmpty() ? new Telemetry() : existing.get(0);
        telemetry.setLogPoint(new LogPoint(t.getLogti()));
        telemetry.setDateTime(dateTime);
        telemetry.setVal(t.getVal());
        return telemetry;
    }


    @Autowired
    private LogPointRepo logPointRepo;

    @Autowired
    private TelemetryRepo telemetryRepo;

    @Autowired
    private LastLoadInfoRepo lastLoadInfoRepo;

    @Autowired
    private OicImpGatewayBuilder oicImpGatewayBuilder;

    @Resource(name="oicPropMap")
    private Map<String, String> oicProperty;
}
