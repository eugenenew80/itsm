package itdesign.web;

import itdesign.entity.Group;
import itdesign.repo.GroupRepo;
import itdesign.web.dto.ErrorDto;
import itdesign.web.dto.GroupDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import static itdesign.util.Util.first;

@RestController
@RequiredArgsConstructor
public class GroupRestController {
    private static final Logger logger = LoggerFactory.getLogger(GroupRestController.class);
    private final GroupRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");

        findById = repo::findOne;
        save = repo::save;
        transformToEntity = t -> mapper.map(t, Group.class);
        transformToDto = t -> mapper.map(t, GroupDto.class);
    }

    @GetMapping(value = "/api/v1/slices/groups", produces = "application/json")
    public List<GroupDto> getAll() {
        logger.debug(getClass().getName() + ".getAll()");

        Sort sort = new Sort(Sort.Direction.ASC, "id");
        return repo.findAll(sort)
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @GetMapping(value = "/api/v1/slices/groups/{id}", produces = "application/json")
    public GroupDto getById(@PathVariable Long id) {
        logger.debug(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    @PostMapping(value = "/api/v1/slices/groups", produces = "application/json")
    public GroupDto create(@RequestBody GroupDto groupDto) {
        logger.debug(getClass().getName() + ".create()");

        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(groupDto);
    }

    @PutMapping(value = "/api/v1/slices/groups/{id}", produces = "application/json")
    public GroupDto update(@PathVariable Long id, @RequestBody GroupDto groupDto) {
        logger.debug(getClass().getName() + ".update()");

        return first(transformToEntity)
            .andThen(save)
            .andThen(transformToDto)
            .apply(groupDto);
    }

    @DeleteMapping(value = "/api/v1/slices/groups/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        logger.debug(getClass().getName() + ".delete()");
        repo.delete(id);
    }

    @ExceptionHandler( { Throwable.class } )
    public ResponseEntity<ErrorDto> handleException(Throwable exc) {
        ErrorDto errorDto = new ErrorDto(exc);
        logger.error( errorDto.getErrType() + ": " + errorDto.getErrDetails());
        logger.trace("view stack trace for details:", exc);
        return new ResponseEntity<>(errorDto,  errorDto.getErrStatus());
    }

    private UnaryOperator<Group> save;
    private Function<Long, Group> findById;
    private Function<GroupDto, Group> transformToEntity;
    private Function<Group, GroupDto> transformToDto;
}
