package itdesign.web;

import io.swagger.annotations.*;
import itdesign.entity.SheetCode;
import itdesign.repo.SheetCodeRepo;
import itdesign.web.dto.SheetCodeDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(tags = "API для работы с кодами листов")
@RestController
@RequiredArgsConstructor
public class SheetCodeRestController extends BaseController {
    private final SheetCodeRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");
        transformToDto = t -> mapper.map(t, SheetCodeDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @ApiImplicitParams(
        @ApiImplicitParam(name = "sessionKey", value = "Ключ сессии", paramType = "header", dataTypeClass = String.class, example = "admin")
    )
    @GetMapping(value = "/api/v1/{lang}/slices/sheetCodes", produces = "application/json")
    public ResponseEntity<List<SheetCodeDto>> getAll(@PathVariable(value = "lang") @ApiParam(value = "Язык", example = "RU") String lang) {
        List<SheetCodeDto> list = repo.findAllByLang(lang.toUpperCase())
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    private Function<SheetCode, SheetCodeDto> transformToDto;
}
