package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.*;
import itdesign.repo.SliceRepo;
import itdesign.service.*;
import itdesign.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import static itdesign.util.Util.first;
import static java.time.LocalDateTime.*;
import static java.util.stream.Collectors.*;

@Api(tags = "API для работы со срезами")
@RestController
@RequiredArgsConstructor
public class SliceRestController extends BaseController {
    private static final Long DEFAULT_STATUS = 0l;
    private static final Long DELETED_STATUS = 3l;
    private static String DEFAULT_REGION = "19";

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

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/slices", produces = "application/json")
    public List<SliceDto> getAll(@RequestParam(value = "deleted", defaultValue = "false") @ApiParam(value = "Показать удаленные записи", example = "false") boolean deleted) {
        logger.debug(getClass().getName() + ".getAll()");
        logger.trace("deleted: " + deleted);

        Status status = statusService.getStatus(DELETED_STATUS);

        return repo.findAll()
            .stream()
            .filter(t -> deleted  || t.getStatus() == null || !t.getStatus().equals(status))
            .map(transformToDto::apply)
            .collect(toList());
    }

    @ApiOperation(value="Получить масимальный номер записи в базе данных")
    @GetMapping(value = "/api/v1/slices/max", produces = MediaType.APPLICATION_JSON_VALUE)
    public LongDto getMax() {
        logger.debug(getClass().getName() + ".getMax()");
        return new LongDto(9999l);
    }

    @ApiOperation(value="Получить запись по идентификатору")
    @GetMapping(value = "/api/v1/slices/{id}", produces = "application/json")
    public SliceDto getById(@PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id) {
        logger.debug(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    @ApiOperation(value="Заказать формирование срезов")
    @PostMapping(value = "/api/v1/slices", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public List<SliceDto> create(@RequestBody OrderSlicesDto orderSlicesDto) {
        logger.debug(getClass().getName() + ".create()");

        List<Slice> slices = orderSlicesDto.list().stream()
            .map(transformToEntity::apply)
            .collect(toList());

        Status status = statusService.getStatus(DEFAULT_STATUS);
        for (Slice slice : slices) {
            Group group = groupService.getGroup(slice.getGroup().getId());
            slice.setStatus(status);
            slice.setGroup(group);
            slice.setCreatedDate(now());
            if (slice.getRegion() == null || slice.getRegion().isEmpty())
                slice.setRegion(DEFAULT_REGION);
        }
        repo.save(slices);

        return slices.stream()
            .map(transformToDto::apply)
            .collect(toList());
    }

    @ApiOperation(value="Пометить срез с указанным идентификатором как удаленный")
    @DeleteMapping(value = "/api/v1/slices/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id) {
        logger.debug(getClass().getName() + ".delete()");

        Slice slice = repo.findOne(id);
        Status status = statusService.getStatus(DELETED_STATUS);
        slice.setStatus(status);
        repo.save(slice);
    }

    private Function<Long, Slice> findById;
    private Function<Slice, SliceDto> transformToDto;
    private Function<OrderSliceDto, Slice> transformToEntity;
}
