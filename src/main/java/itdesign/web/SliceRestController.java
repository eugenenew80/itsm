package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.*;
import itdesign.repo.SliceRepo;
import itdesign.service.CachedGroupService;
import itdesign.service.CachedStatusService;
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
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;

@Api(tags = "API для работы со срезами")
@RestController
@RequiredArgsConstructor
public class SliceRestController extends BaseController {
    private static final String DEFAULT_LANG = "RU";
    private static final String DEFAULT_STATUS = "6";
    private static final String PRELIMINARY_STATUS = "2";
    private static final String FINAL_STATUS = "1";
    private static final String DELETED_STATUS = "3";
    private static final String CANCEL_STATUS = "4";
    private static final String DEFAULT_REGION = "19";
    private final SliceRepo repo;
    private final DozerBeanMapper mapper;
    private final CachedGroupService groupService;
    private final CachedStatusService statusService;

    @PostConstruct
    private void init() {
        findById = repo::findOne;
        transformToDto = t -> mapper.map(t, SliceDto.class);
        transformToEntity = t -> mapper.map(t, Slice.class);

        beforeTransform = t -> {
            Group group = groupService.getGroup(t.getGroupCode(), ofNullable(t.getLang()).orElse(DEFAULT_LANG).toUpperCase());
            t.setGroup(group);
            Status status = statusService.getStatus(t.getStatusCode(), ofNullable(t.getLang()).orElse(DEFAULT_LANG).toUpperCase());
            t.setStatus(status);
            return t;
        };
    }

    @ApiOperation(value="Получить список групп и статусов в виде древовидного списка")
    @GetMapping(value = "/api/v1/{lang}/slices/parents", produces = "application/json")
    public ResponseEntity<List<GroupSliceDto>> getParents(
        @RequestParam(value = "deleted", defaultValue = "false") @ApiParam(value = "Показать удаленные записи",  example = "false") boolean deleted,
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        List<Slice> slices = repo.findAll();
        slices.forEach(t -> {
            t.setLang(lang);
            beforeTransform.apply(t);
        });

        List<GroupSliceDto> listGrDto = slices.stream()
            .map(t -> {
                GroupSliceDto grDto = new GroupSliceDto();
                grDto.setCode(t.getGroupCode());
                if (t.getGroup() != null)
                    grDto.setName(t.getGroup().getName());
                return grDto;
            })
            .distinct()
            .collect(toList());

        for (GroupSliceDto grDto : listGrDto) {
            List<StatusSliceDto> listStDto = slices.stream()
                .filter(t -> t.getGroup().getCode().equals(grDto.getCode()))
                .map(t -> {
                    StatusSliceDto stDto = new StatusSliceDto();
                    stDto.setCode(t.getStatusCode());
                    stDto.setName(t.getFullStatus());
                    stDto.setStatusYear(t.getYear());
                    return stDto;
                })
                .distinct()
                .collect(toList());

            grDto.setChildren(listStDto);
        }

        return ResponseEntity.ok(listGrDto);
    }

    @ApiOperation(value="Получить список всех записей в виде древовидного списка для указанных статуса, группы и года")
    @GetMapping(value = "/api/v1/{lang}/slices/children", produces = "application/json")
    public ResponseEntity<GroupSliceDto> getChildren(
        @RequestParam(value = "deleted", defaultValue = "false") @ApiParam(value = "Показать удаленные записи",  example = "false") boolean deleted,
        @RequestParam(value = "groupCode")  @ApiParam(value = "Код группы отчетов",  example = "001") String groupCode,
        @RequestParam(value = "statusCode") @ApiParam(value = "Код статуса",  example = "0") String statusCode,
        @RequestParam(value = "year")       @ApiParam(value = "Год",  example = "2019") int year,
        @PathVariable(value = "lang")       @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        //Ищем статус и группу
        Group group = groupService.getGroup(groupCode, lang.toUpperCase());
        Status status = statusService.getStatus(statusCode, lang.toUpperCase());

        //Список срезов
        List<SliceDto> list = repo.findAllByGroupCodeAndStatusCode(groupCode, statusCode)
            .stream()
            .filter(t -> deleted || t.getStatusCode() == null || !t.getStatusCode().equals(DELETED_STATUS))
            .filter(t -> t.getStartDate().getYear() == year)
            .map(t -> { t.setLang(lang); return t; })
            .map(beforeTransform::apply)
            .map(transformToDto::apply)
            .collect(toList());

        //Возвращаем в виде дерева
        GroupSliceDto grDto = new GroupSliceDto();
        grDto.setCode(group.getCode());
        grDto.setName(group.getName());

        StatusSliceDto stDto = new StatusSliceDto();
        stDto.setCode(status.getCode());
        stDto.setName(status.getName() + " " + year);
        stDto.setStatusYear(year);
        grDto.setChildren(asList(stDto));

        stDto.setChildren(list);
        return ResponseEntity.ok(grDto);
    }

    @ApiOperation(value="Получить список всех записей в виде простого списка для указанных статуса, группы и года")
    @GetMapping(value = "/api/v1/{lang}/slices", produces = "application/json")
    public ResponseEntity<List<SliceDto>> getAll(
        @RequestParam(value = "deleted", defaultValue = "false") @ApiParam(value = "Показать удаленные записи",  example = "false") boolean deleted,
        @RequestParam(value = "groupCode")  @ApiParam(value = "Код группы отчетов",  example = "001") String groupCode,
        @RequestParam(value = "statusCode") @ApiParam(value = "Код статуса",  example = "0") String statusCode,
        @RequestParam(value = "year")       @ApiParam(value = "Год",  example = "2019") int year,
        @PathVariable(value = "lang")       @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        List<SliceDto> retList = repo.findAllByGroupCodeAndStatusCode(groupCode, statusCode)
            .stream()
            .filter(t -> deleted || t.getStatusCode() == null || !t.getStatusCode().equals(DELETED_STATUS))
            .filter(t -> t.getStartDate().getYear() == year)
            .map(t -> { t.setLang(lang); return t; })
            .map(beforeTransform)
            .map(transformToDto)
            .collect(toList());

        return ResponseEntity.ok(retList);
    }

    @ApiOperation(value="Получить масимальный номер записи в базе данных")
    @GetMapping(value = "/api/v1/{lang}/slices/max", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LongDto> getMax(@PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang) {
        return ResponseEntity.ok(new LongDto(9999l));
    }

    @ApiOperation(value="Получить запись по идентификатору")
    @GetMapping(value = "/api/v1/{lang}/slices/{id}", produces = "application/json")
    public ResponseEntity<SliceDto> getById(
        @PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id,
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        SliceDto retVal = first(findById)
            .andThen(t -> { t.setLang(lang);return t; })
            .andThen(beforeTransform)
            .andThen(transformToDto)
            .apply(id);

        return ResponseEntity.ok(retVal);
    }

    @ApiOperation(value="Заказать формирование срезов")
    @PostMapping(value = "/api/v1/{lang}/slices", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<SliceDto>> create(
        @RequestBody OrderSlicesDto orderSlicesDto,
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
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

        List<SliceDto> retList = slices.stream()
            .map(t -> { t.setLang(lang); return t; })
            .map(beforeTransform)
            .map(transformToDto)
            .collect(toList());

        return ResponseEntity.ok(retList);
    }

    @ApiOperation(value="Пометить срез с указанным идентификатором как удаленный")
    @PutMapping(value = "/api/v1/{lang}/slices/{id}/delete")
    public ResponseEntity<Void> delete(
        @PathVariable(value = "id") @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id,
        @PathVariable(value = "lang") @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        Slice slice = repo.findOne(id);
        slice.setStatusCode(DELETED_STATUS);
        repo.save(slice);

        return ResponseEntity.noContent()
            .build();
    }

    @ApiOperation(value="Пометить срез с указанным идентификатором как предварительный")
    @PutMapping(value = "/api/v1/{lang}/slices/{id}/preliminary")
    public ResponseEntity<Void> preliminary(
        @PathVariable(value = "id") @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id,
        @PathVariable(value = "lang") @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        Slice slice = repo.findOne(id);
        slice.setStatusCode(PRELIMINARY_STATUS);
        repo.save(slice);

        return ResponseEntity.noContent()
            .build();
    }

    @ApiOperation(value="Пометить срез с указанным идентификатором как окончательный")
    @PutMapping(value = "/api/v1/{lang}/slices/{id}/confirm")
    public ResponseEntity<Void> confirm(
        @PathVariable(value = "id") @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id,
        @PathVariable(value = "lang") @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        Slice slice = repo.findOne(id);
        slice.setStatusCode(FINAL_STATUS);
        repo.save(slice);

        return ResponseEntity.noContent()
            .build();
    }

    @ApiOperation(value="Пометить срез с указанным идентификатором как отмененный")
    @PutMapping(value = "/api/v1/{lang}/slices/{id}/cancel")
    public ResponseEntity<Void> cancel(
        @PathVariable(value = "id") @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id,
        @PathVariable(value = "lang") @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        Slice slice = repo.findOne(id);
        slice.setStatusCode(CANCEL_STATUS);
        repo.save(slice);

        return ResponseEntity.noContent()
            .build();
    }

    @ApiOperation(value="Отправить срез с указанным идентификатором на согласование")
    @PutMapping(value = "/api/v1/{lang}/slices/{id}/send")
    public ResponseEntity<Void> send(
        @PathVariable(value = "id") @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id,
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        return ResponseEntity.noContent()
            .build();
    }

    @ApiOperation(value="Согласовать срез с указанным идентификатором")
    @PutMapping(value = "/api/v1/{lang}/slices/{id}/approve")
    public ResponseEntity<Void> approve(
        @PathVariable(value = "id") @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id,
        @PathVariable(value = "lang") @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        return ResponseEntity.noContent()
            .build();
    }

    private Function<Long, Slice> findById;
    private Function<Slice, SliceDto> transformToDto;
    private Function<Slice, Slice> beforeTransform;
    private Function<OrderSliceDto, Slice> transformToEntity;
}