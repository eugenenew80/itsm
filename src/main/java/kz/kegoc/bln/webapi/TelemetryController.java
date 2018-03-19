package kz.kegoc.bln.webapi;

import kz.kegoc.bln.entity.Telemetry;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import static kz.kegoc.bln.util.Util.first;
import static kz.kegoc.bln.util.Util.stream;

@RestController
public class TelemetryController {

    @PostConstruct
    private void init() {
        findById = repo::findOne;
        transformToEntity = t -> mapper.map(t, Telemetry.class);
        transformToDto = t -> mapper.map(t, TelemetryDto.class);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/telemetry", produces = "application/json")
    public List<TelemetryDto> getAll() {
        return stream(repo.findAll())
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/telemetry/{id}", produces = "application/json")
    public TelemetryDto getById(@PathVariable Long id) {
        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }


    private Function<Long, Telemetry> findById;
    private Function<TelemetryDto, Telemetry> transformToEntity;
    private Function<Telemetry, TelemetryDto> transformToDto;


    @Autowired
    private TelemetryRepo repo;

    @Autowired
    private DozerBeanMapper mapper;
}
