package kz.kegoc.bln.webapi;

import kz.kegoc.bln.entity.MeteringPoint;
import kz.kegoc.bln.entity.Telemetry;
import kz.kegoc.bln.repo.MeteringPointRepo;
import kz.kegoc.bln.repo.TelemetryRepo;
import kz.kegoc.bln.webapi.dto.TelemetryDto;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class TelemetryController {

    @PostConstruct
    public void init() {
        MeteringPoint meteringPoint = new MeteringPoint();
        meteringPoint.setId(1l);
        meteringPoint.setName("Точка 1");
        meteringPointRepo.save(meteringPoint);

        Telemetry telemetry = new Telemetry();
        telemetry.setMeteringPoint(meteringPointRepo.findOne(1L));
        telemetry.setMeteringDate(LocalDateTime.now());
        telemetry.setVal(123d);
        repo.save(telemetry);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/telemetry")
    public List<TelemetryDto> getAll() {
        List<TelemetryDto> list = StreamSupport.stream(repo.findAll().spliterator(), false)
            .map(t -> mapper.map(t, TelemetryDto.class))
            .collect(Collectors.toList());

        return list;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/telemetry/{id}")
    public TelemetryDto getOne(@PathVariable Long id) {
        return mapper.map(repo.findOne(id), TelemetryDto.class);
    }

    @Autowired
    private TelemetryRepo repo;

    @Autowired
    private MeteringPointRepo meteringPointRepo;

    @Autowired
    private DozerBeanMapper mapper;
}
