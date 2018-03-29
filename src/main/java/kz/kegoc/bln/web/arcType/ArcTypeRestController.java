package kz.kegoc.bln.web.arcType;

import kz.kegoc.bln.entity.ArcType;
import kz.kegoc.bln.repo.ArcTypeRepo;
import kz.kegoc.bln.web.dto.ArcTypeDto;
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
public class ArcTypeRestController {
    private final ArcTypeRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        findById = repo::findOne;
        save = repo::save;
        transformToEntity = t -> mapper.map(t, ArcType.class);
        transformToDto = t -> mapper.map(t, ArcTypeDto.class);
    }

    @GetMapping(value = "/rest/arcTypes", produces = "application/json")
    public List<ArcTypeDto> getAll() {
        return repo.findAll()
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @GetMapping(value = "/rest/arcTypes/{code}", produces = "application/json")
    public ArcTypeDto getById(@PathVariable String code) {
        return first(findById)
            .andThen(transformToDto)
            .apply(code);
    }

    @PostMapping(value = "/rest/arcTypes", produces = "application/json")
    public ArcTypeDto create(@RequestBody ArcTypeDto dto) {
        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(dto);
    }

    @PutMapping(value = "/rest/arcTypes/{code}", produces = "application/json")
    public ArcTypeDto update(@PathVariable String code, @RequestBody ArcTypeDto dto) {
        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(dto);
    }

    @DeleteMapping(value = "/rest/arcTypes/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String code) {
        repo.delete(code);
    }


    private UnaryOperator<ArcType> save;
    private Function<String, ArcType> findById;
    private Function<ArcTypeDto, ArcType> transformToEntity;
    private Function<ArcType, ArcTypeDto> transformToDto;
}
