package kz.kegoc.bln.web.logPoint;

import kz.kegoc.bln.entity.LogPoint;
import kz.kegoc.bln.repo.LogPointRepo;
import kz.kegoc.bln.web.dto.LogPointDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import static kz.kegoc.bln.util.Util.first;

@RestController
@RequiredArgsConstructor
public class LogPointRestController {
    private final LogPointRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        findById = repo::findOne;
        save = repo::save;
        transformToEntity = t -> mapper.map(t, LogPoint.class);
        transformToDto = t -> mapper.map(t, LogPointDto.class);
    }

    @GetMapping(value = "/rest/logPoints", produces = "application/json")
    public List<LogPointDto> getAll() {
        return repo.findAll()
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @GetMapping(value = "/rest/logPoints/{id}", produces = "application/json")
    public LogPointDto getById(@PathVariable Long id) {
        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    @PostMapping(value = "/rest/logPoints", produces = "application/json")
    public LogPointDto create(@RequestBody LogPointDto meteringPointDto) {
        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(meteringPointDto);
    }

    @PutMapping(value = "/rest/logPoints/{id}", produces = "application/json")
    public LogPointDto update(@PathVariable Long id, @RequestBody LogPointDto meteringPointDto) {
        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(meteringPointDto);
    }

    @DeleteMapping(value = "/rest/logPoints/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repo.delete(id);
    }


    private UnaryOperator<LogPoint> save;
    private Function<Long, LogPoint> findById;
    private Function<LogPointDto, LogPoint> transformToEntity;
    private Function<LogPoint, LogPointDto> transformToDto;
}
