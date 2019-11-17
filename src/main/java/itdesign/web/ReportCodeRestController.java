package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.ReportCode;
import itdesign.repo.ReportCodeRepo;
import itdesign.web.dto.ReportCodeDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import static itdesign.util.Util.first;

@Api(tags = "API для работы с кодами отчётов")
@RestController
@RequiredArgsConstructor
public class ReportCodeRestController extends BaseController {
    private final ReportCodeRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");

        findById = repo::findOne;
        transformToDto = t -> mapper.map(t, ReportCodeDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/{lang}/slices/reportCodes", produces = "application/json")
    public List<ReportCodeDto> getAll(@PathVariable(value = "lang") @ApiParam(value = "Язык", example = "RU") String lang) {
        logger.debug(getClass().getName() + ".getAll()");

        return repo.findAllByLang(lang.toUpperCase())
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @ApiOperation(value="Получить запись по идентификатору")
    @GetMapping(value = "/api/v1/{lang}/slices/reportCodes/{id}", produces = "application/json")
    public ReportCodeDto getById(
        @PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id,
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        logger.debug(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    private Function<Long, ReportCode> findById;
    private Function<ReportCode, ReportCodeDto> transformToDto;
}
