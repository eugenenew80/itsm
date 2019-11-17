package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.TemplateCode;
import itdesign.repo.TemplateCodeRepo;
import itdesign.web.dto.TemplateCodeDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import static itdesign.util.Util.first;

@Api(tags = "API для работы с кодами шаблонов")
@RestController
@RequiredArgsConstructor
public class TemplateCodeRestController extends BaseController {
    private final TemplateCodeRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");

        findById = repo::findOne;
        transformToDto = t -> mapper.map(t, TemplateCodeDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/{lang}/slices/templateCodes", produces = "application/json")
    public List<TemplateCodeDto> getAll(@PathVariable(value = "lang") @ApiParam(value = "Язык", example = "RU") String lang) {
        logger.debug(getClass().getName() + ".getAll()");

        return repo.findAllByLang(lang.toUpperCase())
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @ApiOperation(value="Получить запись по идентификатору")
    @GetMapping(value = "/api/v1/{lang}/slices/templateCodes/{id}", produces = "application/json")
    public TemplateCodeDto getById(
        @PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id,
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        logger.debug(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    private Function<Long, TemplateCode> findById;
    private Function<TemplateCode, TemplateCodeDto> transformToDto;
}
