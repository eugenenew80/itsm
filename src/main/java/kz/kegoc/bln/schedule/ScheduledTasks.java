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
            oicImpGateway
                .config(oicConfigBuilder(oicProperty).build())
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
                curTime = curTime.plusSeconds(lastLoadInfo.getStep());
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
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

    private List<Long> buildPoints() {
        return logPointRepo.findByIsActiveTrue()
            .stream()
            .map(t -> t.getId())
            .collect(Collectors.toList());
    }

    private void save(LocalDateTime dateTime, List<TelemetryRaw> telemetryRawList) {
        logger.debug("ScheduledTasks.startImport save");
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
        logger.debug("ScheduledTasks.startImport completed");
    }


    @Autowired
    public void setLogPointRepo(LogPointRepo logPointRepo) { this.logPointRepo = logPointRepo; }

    @Autowired
    public void setTelemetryRepo(TelemetryRepo telemetryRepo) { this.telemetryRepo = telemetryRepo; }

    @Autowired
    public void setLastLoadInfoRepo(LastLoadInfoRepo lastLoadInfoRepo) { this.lastLoadInfoRepo = lastLoadInfoRepo; }

    @Autowired
    public void setOicImpGateway(OicImpGateway oicImpGateway) { this.oicImpGateway = oicImpGateway; }

    @Resource(name="oicPropMap")
    public void setOicProperty(Map<String, String> oicProperty) { this.oicProperty = oicProperty; }

    private LogPointRepo logPointRepo;
    private TelemetryRepo telemetryRepo;
    private LastLoadInfoRepo lastLoadInfoRepo;
    private OicImpGateway oicImpGateway;
    private Map<String, String> oicProperty;
}
