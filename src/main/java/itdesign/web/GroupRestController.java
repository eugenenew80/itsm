package itdesign.web;

import io.swagger.annotations.*;
import itdesign.entity.Group;
import itdesign.repo.GroupRepo;
import itdesign.web.dto.GroupDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(tags = "API для работы с группами отчётов")
@RestController
@RequiredArgsConstructor
public class GroupRestController extends BaseController {
    private static final String className = GroupRestController.class.getName();
    private final GroupRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        transformToDto = t -> mapper.map(t, GroupDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @ApiImplicitParams(
        @ApiImplicitParam(name = "sessionKey", value = "Ключ сессии", paramType = "header", dataTypeClass = String.class, example = "admin")
    )
    @GetMapping(value = "/api/v1/{lang}/slices/groups", produces = "application/json")
    public ResponseEntity<List<GroupDto>> getAll(@PathVariable(value = "lang") @ApiParam(value = "Язык", example = "RU") String lang) {
        List<GroupDto> list = repo.findAllByLang(lang.toUpperCase())
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());

        return ResponseEntity
            .ok(list);
    }

    private Function<Group, GroupDto> transformToDto;
}
