package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.Region;
import itdesign.repo.RegionRepo;
import itdesign.web.dto.RegionDto;
import itdesign.web.dto.RegionTreeDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(tags = "API для работы с регионами")
@RestController
@RequiredArgsConstructor
public class RegionRestController extends BaseController {
    private static final String className = RegionRestController.class.getName();
    private final RegionRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        logger.debug(className + ".init()");
        transformToDto = t -> mapper.map(t, RegionDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/{lang}/slices/regs", produces = "application/json")
    public List<RegionDto> getAll(@PathVariable(value = "lang") @ApiParam(value = "Язык", example = "RU") String lang) {
        logger.debug(className + ".getAll()");
        logger.trace("lang: " + lang);

        return repo.findAllByLang(lang.toUpperCase())
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @ApiOperation(value="Получить список всех записей в виде дерева")
    @GetMapping(value = "/api/v1/{lang}/slices/regsTree", produces = "application/json")
    public RegionTreeDto getTree(@PathVariable(value = "lang") @ApiParam(value = "Язык", example = "RU") String lang) {
        logger.debug(className + ".getTree()");
        logger.trace("lang: " + lang);

        //Получаем список регионов
        List<Region> list = new ArrayList<>(repo.findAllByLang(lang));
        if (list.size() == 0)
            return new RegionTreeDto();

        //Корневой элемент
        Region rootEntity = list.stream()
            .filter(t -> t.getRegType().equals("00"))
            .findFirst()
            .orElseGet(null);

        if (rootEntity == null)
            return null;

        RegionTreeDto rootDto = new RegionTreeDto();
        rootDto.setCode(rootEntity.getCode());
        rootDto.setName(rootEntity.getName());

        //Уровень 1
        List<Region> childrenLev1 = list.stream()
            .filter(t -> t.getRegType().equals("01") && t.getCode().substring(0, 2).equals(rootEntity.getCode()))
            .collect(Collectors.toList());

        List<RegionTreeDto> childrenDtoLev1 = new ArrayList<>();
        for (Region r : childrenLev1) {
            RegionTreeDto rd = new RegionTreeDto();
            rd.setCode(r.getCode());
            rd.setName(r.getName());
            childrenDtoLev1.add(rd);
        }
        rootDto.setChildren(childrenDtoLev1);


        //Уровень 2
        for (RegionTreeDto rd1 : rootDto.getChildren()) {
            List<Region> childrenLev2 = list.stream()
                .filter(t -> t.getRegType().equals("02") && t.getCode().substring(0, 4).equals(rd1.getCode()))
                .collect(Collectors.toList());

            List<RegionTreeDto> childrenDtoLev2 = new ArrayList<>();
            for (Region r : childrenLev2) {
                RegionTreeDto rd2 = new RegionTreeDto();
                rd2.setCode(r.getCode());
                rd2.setName(r.getName());
                rd2.setChildren(new ArrayList<>());
                childrenDtoLev2.add(rd2);
            }
            rd1.setChildren(childrenDtoLev2);
        }

        return rootDto;
    }

    private Function<Region, RegionDto> transformToDto;
}
