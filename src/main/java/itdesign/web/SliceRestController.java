package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.*;
import itdesign.repo.GroupRepo;
import itdesign.repo.SliceRepo;
import itdesign.repo.StatusRepo;
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
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;

@Api(tags = "API для работы со срезами")
@RestController
@RequiredArgsConstructor
public class SliceRestController extends BaseController {
    private static final String DEFAULT_LANG = "RU";
    private static final String DEFAULT_STATUS = "0";
    private static final String DELETED_STATUS = "3";
    private static String DEFAULT_REGION = "19";
    private final SliceRepo repo;
    private final GroupRepo groupRepo;
    private final StatusRepo statusRepo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");

        findById = repo::findOne;
        transformToDto = t -> mapper.map(t, SliceDto.class);
        transformToDto2 = t -> mapper.map(t, GroupAndStatusDto.class);
        transformToEntity = t -> mapper.map(t, Slice.class);

        beforeTransform = t -> {
            Group group = groupRepo.findByCodeAndLang(t.getGroupCode(), ofNullable(t.getLang()).orElse(DEFAULT_LANG).toUpperCase());
            t.setGroup(group);
            Status status = statusRepo.findByCodeAndLang(t.getStatusCode(), ofNullable(t.getLang()).orElse(DEFAULT_LANG).toUpperCase());
            t.setStatus(status);
            return t;
        };
    }

    @ApiOperation(value="Получить список групп и статусов")
    @GetMapping(value = "/api/v1/{lang}/slices/groupsAndStatuses", produces = "application/json")
    public List<GroupAndStatusDto> getGroupsAndStatuses(
        @RequestParam(value = "deleted", defaultValue = "false") @ApiParam(value = "Показать удаленные записи",  example = "false") boolean deleted,
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        logger.debug(getClass().getName() + ".getGroupsAndStatuses()");
        logger.trace("deleted: " + deleted);

        return repo.findAll()
            .stream()
            .filter(t -> deleted  || t.getStatusCode() == null || !t.getStatusCode().equals(DELETED_STATUS))
            .map(t ->  { t.setLang(lang); return t; } )
            .map(beforeTransform::apply)
            .map(transformToDto2::apply)
            .distinct()
            .collect(toList());
    }

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/{lang}/slices", produces = "application/json")
    public List<SliceDto> getAll(
        @RequestParam(value = "deleted", defaultValue = "false") @ApiParam(value = "Показать удаленные записи",  example = "false") boolean deleted,
        @RequestParam(value = "groupCode")  @ApiParam(value = "Код группы отчетов",  example = "001") String groupCode,
        @RequestParam(value = "statusCode") @ApiParam(value = "Код статуса",  example = "0") String statusCode,
        @RequestParam(value = "year")       @ApiParam(value = "Год",  example = "2019") int year,
        @PathVariable(value = "lang")       @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        logger.debug(getClass().getName() + ".getAll()");
        logger.trace("deleted: " + deleted);
        logger.trace("groupCode: " + groupCode);
        logger.trace("statusCode: " + statusCode);
        logger.trace("year: " + year);

        return repo.findAllByGroupCodeAndStatusCode(groupCode, statusCode)
            .stream()
            .filter(t -> deleted  || t.getStatusCode() == null || !t.getStatusCode().equals(DELETED_STATUS))
            .filter(t -> t.getStartDate().getYear() == year)
            .map(t ->  { t.setLang(lang); return t; } )
            .map(beforeTransform::apply)
            .map(transformToDto::apply)
            .collect(toList());
    }


    @ApiOperation(value="Получить масимальный номер записи в базе данных")
    @GetMapping(value = "/api/v1/{lang}/slices/max", produces = MediaType.APPLICATION_JSON_VALUE)
    public LongDto getMax(@PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang) {
        logger.debug(getClass().getName() + ".getMax()");
        return new LongDto(9999l);
    }

    @ApiOperation(value="Получить запись по идентификатору")
    @GetMapping(value = "/api/v1/{lang}/slices/{id}", produces = "application/json")
    public SliceDto getById(
        @PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id,
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        logger.debug(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(t ->  { t.setLang(lang); return t; } )
            .andThen(beforeTransform)
            .andThen(transformToDto)
            .apply(id);
    }

    @ApiOperation(value="Заказать формирование срезов")
    @PostMapping(value = "/api/v1/{lang}/slices", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public List<SliceDto> create(
        @RequestBody OrderSlicesDto orderSlicesDto,
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        logger.debug(getClass().getName() + ".create()");

        List<Slice> slices = orderSlicesDto.list().stream()
            .map(transformToEntity::apply)
            .collect(toList());

        for (Slice slice : slices) {
            slice.setStatusCode(DEFAULT_STATUS);
            slice.setCreatedDate(now());
            if (slice.getRegion() == null || slice.getRegion().isEmpty())
                slice.setRegion(DEFAULT_REGION);
        }
        repo.save(slices);

        return slices.stream()
            .map(t ->  { t.setLang(lang); return t; } )
            .map(beforeTransform::apply)
            .map(transformToDto::apply)
            .collect(toList());
    }

    @ApiOperation(value="Пометить срез с указанным идентификатором как удаленный")
    @DeleteMapping(value = "/api/v1/{lang}/slices/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id,
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        logger.debug(getClass().getName() + ".delete()");

        Slice slice = repo.findOne(id);
        slice.setStatusCode(DELETED_STATUS);
        repo.save(slice);
    }

    private Function<Long, Slice> findById;
    private Function<Slice, SliceDto> transformToDto;
    private Function<Slice, GroupAndStatusDto> transformToDto2;
    private Function<Slice, Slice> beforeTransform;
    private Function<OrderSliceDto, Slice> transformToEntity;
}