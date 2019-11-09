package itdesign.web;

import itdesign.entity.*;
import itdesign.repo.SliceRepo;
import itdesign.service.*;
import itdesign.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import static itdesign.util.Util.first;
import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class SliceRestController {
    private static final Logger logger = LoggerFactory.getLogger(SliceRestController.class);
    private final SliceRepo repo;
    private final CachedStatusService statusService;
    private final CachedGroupService groupService;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");

        findById = repo::findOne;
        transformToDto = t -> mapper.map(t, SliceDto.class);
        transformToEntity = t -> mapper.map(t, Slice.class);
    }

    @GetMapping(value = "/api/v1/slices", produces = "application/json")
    public List<SliceDto> getAll(@RequestParam(value = "deleted", defaultValue = "false") boolean deleted) {
        logger.debug(getClass().getName() + ".getAll()");
        logger.debug("deleted: " + deleted);

        Status status = statusService.getStatus(3l);

        return repo.findAll()
            .stream()
            .filter(t -> deleted  || t.getStatus() == null || !t.getStatus().equals(status))
            .map(transformToDto::apply)
            .collect(toList());
    }

    @GetMapping(value = "/api/v1/slices/max", produces = MediaType.APPLICATION_JSON_VALUE)
    public LongDto getMax() {
        logger.debug(getClass().getName() + ".getMax()");

        return new LongDto(9999l);
    }

    @GetMapping(value = "/api/v1/slices/{id}", produces = "application/json")
    public SliceDto getById(@PathVariable Long id) {
        logger.debug(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    @PostMapping(value = "/api/v1/slices", produces = "application/json")
    public List<SliceDto> create(@RequestBody OrderSlicesDto orderSlicesDto) {
        logger.debug(getClass().getName() + ".create()");

        List<Slice> slices = orderSlicesDto.list().stream()
            .map(transformToEntity::apply)
            .collect(toList());

        Status status = statusService.getStatus(0l);
        for (Slice slice : slices) {
            Group group = groupService.getGroup(slice.getGroup().getId());
            slice.setStatus(status);
            slice.setGroup(group);
        }
        repo.save(slices);

        return slices.stream()
            .map(transformToDto::apply)
            .collect(toList());
    }

    @DeleteMapping(value = "/api/v1/slices/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        logger.debug(getClass().getName() + ".delete()");

        Slice slice = repo.findOne(id);
        Status status = statusService.getStatus(3l);
        slice.setStatus(status);
        repo.save(slice);
    }

    @ExceptionHandler( { Throwable.class } )
    public ResponseEntity<ErrorDto> handleException(Throwable exc) {
        ErrorDto errorDto = new ErrorDto(exc);
        logger.error( errorDto.getErrType() + ": " + errorDto.getErrDetails());
        logger.trace("view stack trace for details:", exc);
        return new ResponseEntity<>(errorDto,  errorDto.getErrStatus());
    }

    private Function<Long, Slice> findById;
    private Function<Slice, SliceDto> transformToDto;
    private Function<OrderSliceDto, Slice> transformToEntity;
}
