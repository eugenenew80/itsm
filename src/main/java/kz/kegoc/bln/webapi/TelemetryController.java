package kz.kegoc.bln.webapi;

import kz.kegoc.bln.entity.Telemetry;
import kz.kegoc.bln.repo.TelemetryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TelemetryController {

    @PostConstruct
    public void init() {
        Telemetry telemetry = new Telemetry();
        telemetry.setMeteringPointId(1l);
        telemetry.setMeteringDate(LocalDateTime.now());
        telemetry.setVal(123d);
        repo.save(telemetry);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/telemetry")
    public List<Telemetry> getAll() {
        List<Telemetry> list = new ArrayList<>();
        repo.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/telemetry/{id}")
    public Telemetry getOne(@PathVariable Long id) {
        return repo.findOne(id);
    }

    @Autowired
    private TelemetryRepo repo;
}
