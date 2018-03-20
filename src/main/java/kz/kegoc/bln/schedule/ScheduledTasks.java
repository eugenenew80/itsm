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
        LastLoadInfo lastLoadInfo = lastLoadInfoRepo.findOne("SEC-5");
        Long defStep = 5l;
        if (lastLoadInfo==null) {
            lastLoadInfo = new LastLoadInfo();
            lastLoadInfo.setArcType("SEC-5");
            lastLoadInfo.setStep(defStep);
            lastLoadInfo.setLastLoadTime(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
        }

        LocalDateTime curTime = lastLoadInfo.getLastLoadTime();
        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        try {
            oicImpGateway
                .config(propConfig())
                .points(buildPoints());

            while (curTime.isBefore(endTime) || curTime.isEqual(endTime)) {
                List<TelemetryRaw> telemetry = oicImpGateway.request(curTime);
                save(curTime, telemetry);
                lastLoadInfo.setLastLoadTime(curTime);
                lastLoadInfoRepo.save(lastLoadInfo);
                curTime = curTime.plusSeconds(defStep);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private List<Long> buildPoints() {
        return logPointRepo.findByIsActiveTrue()
            .stream()
            .map(t -> t.getId())
            .collect(Collectors.toList());
    }

    private void save(LocalDateTime dateTime, List<TelemetryRaw> telemetryRawList) {
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
