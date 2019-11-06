package itdesign.web;

import itdesign.entity.Status;
import itdesign.repo.StatusRepo;
import itdesign.web.dto.StatusDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static itdesign.util.Util.first;

@RestController
@RequiredArgsConstructor
public class StatusRestController {
    private static final Logger logger = LoggerFactory.getLogger(StatusRestController.class);
    private final StatusRepo repo;
    private final DozerBeanMapper mapper;


    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");

        findById = repo::findOne;
        save = repo::save;
        transformToEntity = t -> mapper.map(t, Status.class);
        transformToDto = t -> mapper.map(t, StatusDto.class);
    }

    @GetMapping(value = "/api/v1/slices/statuses", produces = "application/json")
    public List<StatusDto> getAll() {
        logger.info(getClass().getName() + ".getAll()");

        Sort sort = new Sort(Sort.Direction.ASC, "id");
        return repo.findAll(sort)
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @GetMapping(value = "/api/v1/slices/statuses/{id}", produces = "application/json")
    public StatusDto getById(@PathVariable Long id) {
        logger.info(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    @PostMapping(value = "/api/v1/slices/statuses", produces = "application/json")
    public StatusDto create(@RequestBody StatusDto statusDto) {
        logger.info(getClass().getName() + ".create()");

        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(statusDto);
    }

    @PutMapping(value = "/api/v1/slices/statuses/{id}", produces = "application/json")
    public StatusDto update(@PathVariable Long id, @RequestBody StatusDto statusDto) {
        logger.info(getClass().getName() + ".update()");

        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(statusDto);
    }

    @DeleteMapping(value = "/api/v1/slices/statuses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        logger.info(getClass().getName() + ".delete()");
        repo.delete(id);
    }

    private UnaryOperator<Status> save;
    private Function<Long, Status> findById;
    private Function<StatusDto, Status> transformToEntity;
    private Function<Status, StatusDto> transformToDto;
}
