package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.Group;
import itdesign.repo.GroupRepo;
import itdesign.web.dto.GroupDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(tags = "API для работы с группами отчётов")
@RestController
@RequiredArgsConstructor
public class GroupRestController {//extends BaseController {
    private final GroupRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        //logger.debug(getClass() .getName()+ ".init()");
        transformToDto = t -> mapper.map(t, GroupDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/{lang}/slices/groups", produces = "application/json")
    public List<GroupDto> getAll(@PathVariable(value = "lang") @ApiParam(value = "Язык", example = "RU") String lang) {
        //logger.debug(getClass().getName() + ".getAll()");

        return repo.findAllByLang(lang.toUpperCase())
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    private Function<Group, GroupDto> transformToDto;
}
