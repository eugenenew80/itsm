package kz.kegoc.bln;

import kz.kegoc.bln.gateway.oic.OicDataGatewayImpl;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static kz.kegoc.bln.gateway.oic.OicConfig.defaultConfig;

@EntityScan(
    basePackageClasses = { App.class, Jsr310JpaConverters.class }
)
@SpringBootApplication
@EnableScheduling
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        List<Long> points = Arrays.asList(1L, 2L);
        LocalDateTime requestedTime = LocalDateTime.parse("19.03.2018 05:05:00", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        try {
            List<TelemetryRaw> telemetry = new OicDataGatewayImpl()
                .config(defaultConfig())
                .points(points)
                .request(requestedTime);

            telemetry.stream().forEach(t -> logger.info(telemetry.toString()) );
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        SpringApplication.run(App.class, args);
    }
}
