package kz.kegoc.bln;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.OicDataGatewayImpl;
import kz.kegoc.bln.gateway.oic.TelemetryRaw;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@EntityScan(
    basePackageClasses = { App.class, Jsr310JpaConverters.class }
)
@SpringBootApplication
@EnableScheduling
public class App {
    public static void main(String[] args) {

        OicConfig config = new OicConfig.Builder()
            .server1("OIC01UG.CORP.KEGOC.KZ")
            .server2("OIC02UG.CORP.KEGOC.KZ")
            .port(1433)
            .user("bln")
            .pass("123456")
            .masterDb("MASTER")
            .oicDb("OICDB")
            .build();

        List<Long> points = Arrays.asList(1L, 2L);
        LocalDateTime requestedTime = LocalDateTime.parse("19.03.2018 05:05:00", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        try {
            List<TelemetryRaw> telemetry = new OicDataGatewayImpl()
                .config(config)
                .points(points)
                .request(requestedTime);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        SpringApplication.run(App.class, args);
    }
}
