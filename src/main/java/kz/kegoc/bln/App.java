package kz.kegoc.bln;

import kz.kegoc.bln.gateway.oic.impl.OicImpGatewayImpl;
import kz.kegoc.bln.gateway.oic.TelemetryRaw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static kz.kegoc.bln.gateway.oic.OicConfig.defaultConfig;
import static kz.kegoc.bln.gateway.oic.OicConfig.defaultPoints;

@EntityScan(
    basePackageClasses = { App.class, Jsr310JpaConverters.class }
)
@SpringBootApplication
@EnableScheduling
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LocalDateTime requestedTime = LocalDateTime.parse("19.03.2018 05:05:00", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        try {
            List<TelemetryRaw> telemetry = new OicImpGatewayImpl()
                .config(defaultConfig())
                .points(defaultPoints())
                .request(requestedTime);

            telemetry.stream().forEach(t -> logger.info(telemetry.toString()) );
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        //SpringApplication.run(App.class, args);
    }
}
