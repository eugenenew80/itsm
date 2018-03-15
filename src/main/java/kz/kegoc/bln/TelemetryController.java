package kz.kegoc.bln;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TelemetryController {

    @RequestMapping("/telemetry")
    public List<Telemetry> getAll() {
        List<Telemetry> list = new ArrayList<>();

        Telemetry telemetry = new Telemetry();
        telemetry.setMeteringPointId(1l);
        telemetry.setMeteringDate(LocalDateTime.now());
        telemetry.setVal(123d);
        list.add(telemetry);

        telemetry = new Telemetry();
        telemetry.setMeteringPointId(1l);
        telemetry.setMeteringDate(LocalDateTime.now());
        telemetry.setVal(456d);
        list.add(telemetry);

        return list;
    }

}
