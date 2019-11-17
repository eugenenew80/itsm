package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.TemplateCode;
import itdesign.repo.TemplateCodeRepo;
import itdesign.web.dto.LongDto;
import itdesign.web.dto.TemplateCodeDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dozer.DozerBeanMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    @ApiOperation(value="Импорт данных из файла Excel")
    @PostMapping(value = "/api/v1/{lang}/slices/templateCodes/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importData(@PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang) {
        long count = repo.count();
        if (count > 0)
            return new LongDto(count);

        Long i = 0l;
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
        try {
            Resource resources[] = resolver.getResources("classpath:templates/**");
            for (Resource resource : resources) {
                i++;
                TemplateCode tc = new TemplateCode();
                tc.setName(resource.getFilename());

                int n = tc.getName().indexOf(".");
                tc.setCode(resource.getFilename().substring(0, 6));
                tc.setFileType(resource.getFilename().substring(n+1));

                String l = resource.getFilename().substring(6, 8);
                if (l.equals("_1"))
                    tc.setLang("RU");
                else
                    tc.setLang("KZ");

                tc.setBinaryFile(IOUtils.toByteArray(resource.getInputStream()));
                repo.save(tc);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new LongDto(i);
    }

    private Function<Long, TemplateCode> findById;
    private Function<TemplateCode, TemplateCodeDto> transformToDto;
}
