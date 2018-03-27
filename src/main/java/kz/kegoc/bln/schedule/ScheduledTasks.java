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
    private static final String defArcType = "SEC-180";
    private static final Long   defStep = 180l;
    private boolean isReady = false;

    @Scheduled(cron = "0 */1 * * * *")
    public void startImport() {
        if (!isReady) return;

        logger.info("ScheduledTasks.startImport started");

        LastLoadInfo lastLoadInfo = buildLastLoadInfo();
        LocalDateTime curTime = lastLoadInfo.getLastLoadTime();
        LocalDateTime endTime = LocalDateTime.now()
            .minusMinutes(1)
            .truncatedTo(ChronoUnit.MINUTES);

        logger.info("Period: " + curTime.toString() + " - " + endTime.toString());
        try {
            OicImpGateway oicImpGateway = oicImpGatewayBuilder
                .config(oicConfigBuilder(oicProperty).build())
                .points(buildPoints())
                .build();

            while (curTime.isBefore(endTime) || curTime.isEqual(endTime)) {
                List<TelemetryRaw> telemetries = oicImpGateway.request(curTime);
                save(lastLoadInfo, curTime, telemetries);
                curTime = curTime.plusSeconds(lastLoadInfo.getStep());
            }
        }
        catch (Exception e) {
            logger.error("ScheduledTasks.startImport crashed: " + e.getMessage());
        }

        logger.info("ScheduledTasks.startImport completed");
    }

    private LastLoadInfo buildLastLoadInfo() {
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

    private void save(LastLoadInfo lastLoadInfo, LocalDateTime curTime, List<TelemetryRaw> telemetries) {
        if (telemetries.isEmpty()) {
            logger.warn("No data at: " + curTime.toString());
            return;
        }

        List<Telemetry> list = telemetries.stream()
            .map(t -> toTelemetry(curTime, t))
            .collect(Collectors.toList());
        telemetryRepo.save(list);

        lastLoadInfo.setLastLoadTime(curTime);
        lastLoadInfoRepo.save(lastLoadInfo);
    }

    private Telemetry toTelemetry(LocalDateTime dateTime, TelemetryRaw t) {
        List<Telemetry> existing = telemetryRepo.findByLogPointIdAndDateTime(t.getLogti(), dateTime);
        Telemetry telemetry = existing.isEmpty() ? new Telemetry() : existing.get(0);
        telemetry.setLogPoint(new LogPoint(t.getLogti()));
        telemetry.setDateTime(dateTime);
        telemetry.setVal(t.getVal());
        telemetry.setSystemCode("OIC");
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
    private LastLoadInfoRepo lastLoadInfoRepo;

    @Autowired
    private OicImpGatewayBuilder oicImpGatewayBuilder;

    @Resource(name="oicPropMap")
    private Map<String, String> oicProperty;
}
