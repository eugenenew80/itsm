package kz.kegoc.bln.web;

import kz.kegoc.bln.entity.LogPoint;
import kz.kegoc.bln.entity.Telemetry;
import kz.kegoc.bln.repo.LogPointRepo;
import kz.kegoc.bln.repo.TelemetryRepo;
import kz.kegoc.bln.web.dto.LogPointCfgDto;
import kz.kegoc.bln.web.dto.TelemetryDto;
import kz.kegoc.bln.web.dto.TelemetryExpDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static kz.kegoc.bln.util.Util.first;

@RestController
@RequiredArgsConstructor
public class TelemetryRestController {
    private static final Logger logger = LoggerFactory.getLogger(TelemetryRestController.class);
    private final TelemetryRepo repo;
    private final LogPointRepo logPointRepo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        findById = repo::findOne;
        transformToEntity = t -> mapper.map(t, Telemetry.class);
        transformToDto = t -> mapper.map(t, TelemetryExpDto.class);
    }

    @PostMapping(value = "/rest/exp/telemetry/{arcType}", produces = "application/json")
    public List<TelemetryExpDto> getAll(@PathVariable String arcType, @RequestBody List<LogPointCfgDto> points) {
        points.stream().forEach(point -> {
            LogPoint logPoint = logPointRepo.findOne(point.getLogPointId());
            if (logPoint==null) {
                logPoint = new LogPoint();
                logPoint.setId(point.getLogPointId());
                logPoint.setName("ТИ #" + point.getLogPointId());
                logPointRepo.save(logPoint);
            }
        });

        List<Telemetry> list = points.stream()
            .flatMap(p -> repo.findAllByLogPointIdAndDateTimeBetweenAndArcTypeCode(
                    p.getLogPointId(),
                    p.getStart(),
                    p.getEnd(),
                    arcType
                ).stream())
            .collect(toList());

        return list.stream()
            .map(transformToDto::apply)
            .collect(toList());
    }

    @GetMapping(value = "/rest/telemetry/{id}", produces = "application/json")
    public TelemetryExpDto getById(@PathVariable Long id) {
        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    private Function<Long, Telemetry> findById;
    private Function<TelemetryDto, Telemetry> transformToEntity;
    private Function<Telemetry, TelemetryExpDto> transformToDto;
}
