package kz.kegoc.bln.web.unit;

import kz.kegoc.bln.entity.Unit;
import kz.kegoc.bln.repo.UnitRepo;
import kz.kegoc.bln.web.dto.UnitDto;
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
public class UnitRestController {
    private final UnitRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        findById = repo::findOne;
        save = repo::save;
        transformToEntity = t -> mapper.map(t, Unit.class);
        transformToDto = t -> mapper.map(t, UnitDto.class);
    }

    @GetMapping(value = "/units", produces = "application/json")
    public List<UnitDto> getAll() {
        return repo.findAll()
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @GetMapping(value = "/units/{id}", produces = "application/json")
    public UnitDto getById(@PathVariable Long id) {
        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    @PostMapping(value = "/units", produces = "application/json")
    public UnitDto create(@RequestBody UnitDto meteringPointDto) {
        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(meteringPointDto);
    }

    @PutMapping(value = "/units/{id}", produces = "application/json")
    public UnitDto update(@PathVariable Long id, @RequestBody UnitDto meteringPointDto) {
        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(meteringPointDto);
    }

    @DeleteMapping(value = "/units/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repo.delete(id);
    }


    private UnaryOperator<Unit> save;
    private Function<Long, Unit> findById;
    private Function<UnitDto, Unit> transformToEntity;
    private Function<Unit, UnitDto> transformToDto;
}
