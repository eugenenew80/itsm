package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.SheetCode;
import itdesign.repo.SheetCodeRepo;
import itdesign.web.dto.SheetCodeDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import static itdesign.util.Util.first;

@Api(tags = "API для работы с кодами листов")
@RestController
@RequiredArgsConstructor
public class SheetCodeRestController extends BaseController {
    private final SheetCodeRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");

        findById = repo::findOne;
        transformToDto = t -> mapper.map(t, SheetCodeDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/{lang}/slices/sheetCodes", produces = "application/json")
    public List<SheetCodeDto> getAll(@PathVariable(value = "lang") @ApiParam(value = "Язык", example = "RU") String lang) {
        logger.debug(getClass().getName() + ".getAll()");

        return repo.findAllByLang(lang.toUpperCase())
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @ApiOperation(value="Получить запись по идентификатору")
    @GetMapping(value = "/api/v1/{lang}/slices/sheetCodes/{id}", produces = "application/json")
    public SheetCodeDto getById(
        @PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id,
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        logger.debug(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    private Function<Long, SheetCode> findById;
    private Function<SheetCode, SheetCodeDto> transformToDto;
}
