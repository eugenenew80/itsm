package kz.kegoc.bln.webapi;

import kz.kegoc.bln.entity.LogPoint;
import kz.kegoc.bln.repo.LogPointRepo;
import kz.kegoc.bln.webapi.dto.LogPointDto;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import static kz.kegoc.bln.util.Util.first;
import static kz.kegoc.bln.util.Util.stream;

@RestController
public class LogPointController {

    @PostConstruct
    private void init() {
        findById = repo::findOne;
        save = repo::save;
        transformToEntity = t -> mapper.map(t, LogPoint.class);
        transformToDto = t -> mapper.map(t, LogPointDto.class);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/logPoints", produces = "application/json")
    public List<LogPointDto> getAll() {
        return stream(repo.findAll())
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/logPoints/{id}", produces = "application/json")
    public LogPointDto getById(@PathVariable Long id) {
        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/logPoints", produces = "application/json")
    public LogPointDto create(@RequestBody LogPointDto meteringPointDto) {
        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(meteringPointDto);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/logPoints/{id}", produces = "application/json")
    public LogPointDto update(@PathVariable Long id, @RequestBody LogPointDto meteringPointDto) {
        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(meteringPointDto);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/logPoints/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repo.delete(id);
    }


    private UnaryOperator<LogPoint> save;
    private Function<Long, LogPoint> findById;
    private Function<LogPointDto, LogPoint> transformToEntity;
    private Function<LogPoint, LogPointDto> transformToDto;


    @Autowired
    private LogPointRepo repo;

    @Autowired
    private DozerBeanMapper mapper;
}
