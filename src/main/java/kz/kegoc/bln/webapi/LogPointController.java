package kz.kegoc.bln.webapi;

import kz.kegoc.bln.entity.LogPoint;
import kz.kegoc.bln.repo.LogPointRepo;
import kz.kegoc.bln.webapi.dto.MeteringPointDto;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
        transformToDto = t -> mapper.map(t, MeteringPointDto.class);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/logPoints")
    public List<MeteringPointDto> getAll() {
        return stream(repo.findAll())
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/logPoints/{id}")
    public MeteringPointDto getById(@PathVariable Long id) {
        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/logPoints")
    public MeteringPointDto create(@RequestBody MeteringPointDto meteringPointDto) {
        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(meteringPointDto);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/logPoints/{id}")
    public MeteringPointDto update(@PathVariable Long id, @RequestBody MeteringPointDto meteringPointDto) {
        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(meteringPointDto);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/logPoints/{id}")
    public void delete(@PathVariable Long id) {
        repo.delete(id);
    }


    private Function<Long, LogPoint> findById;
    private UnaryOperator<LogPoint> save;
    private Function<MeteringPointDto, LogPoint> transformToEntity;
    private Function<LogPoint, MeteringPointDto> transformToDto;


    @Autowired
    private LogPointRepo repo;

    @Autowired
    private DozerBeanMapper mapper;
}
