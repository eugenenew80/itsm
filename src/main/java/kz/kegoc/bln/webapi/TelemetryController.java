package kz.kegoc.bln.webapi;

import kz.kegoc.bln.entity.LogPoint;
import kz.kegoc.bln.repo.LogPointRepo;
import kz.kegoc.bln.repo.TelemetryRepo;
import kz.kegoc.bln.webapi.dto.TelemetryDto;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class TelemetryController {

    @PostConstruct
    public void init() {
        LogPoint meteringPoint = new LogPoint();
        meteringPoint.setId(1l);
        meteringPoint.setName("Точка 1");
        logPointRepo.save(meteringPoint);
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
    private LogPointRepo logPointRepo;

    @Autowired
    private DozerBeanMapper mapper;
}
