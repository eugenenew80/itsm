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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dozer.DozerBeanMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    @GetMapping(value = "/api/v1/slices/templateCodes", produces = "application/json")
    public List<TemplateCodeDto> getAll(@RequestParam(value = "lang", defaultValue = "RU") @ApiParam(value = "Язык", example = "RU") String lang) {
        logger.debug(getClass().getName() + ".getAll()");

        Sort sort = new Sort(Sort.Direction.ASC, "id");
        return repo.findAll(sort)
            .stream()
            .filter(t -> t.getLang().equals(lang.toUpperCase()))
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @ApiOperation(value="Получить запись по идентификатору")
    @GetMapping(value = "/api/v1/slices/templateCodes/{id}", produces = "application/json")
    public TemplateCodeDto getById(@PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id) {
        logger.debug(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    @ApiOperation(value="Импорт данных из файла Excel")
    @PostMapping(value = "/api/v1/slices/templateCodes/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importData() {
        long count = repo.count();
        if (count > 0)
            return new LongDto(count);

        int i = 0;
        try (InputStream ExcelFileToRead = new FileInputStream(new ClassPathResource("tblshablon.xlsx").getFile())) {
            Workbook workbook = new XSSFWorkbook(ExcelFileToRead);
            Sheet sheet = workbook.getSheetAt(0);

            List<TemplateCode> list = new ArrayList<>();
            for (Row row : sheet) {
                i++;
                if (i <=2) continue;
                int j = 0;
                String nameRu = "";
                String code = "";
                String fileType = "XLSX";
                for (Cell cell : row) {
                    j++;
                    if (j == 1)
                        nameRu = cell.getStringCellValue();

                    if (nameRu == null || nameRu.isEmpty())
                        continue;

                    if (j == 3)
                        code = new Double(cell.getNumericCellValue()).toString();

                    if (j > 3)
                        continue;
                }

                TemplateCode tc = new TemplateCode();
                tc.setCode(code);
                tc.setLang("RU");
                tc.setName(nameRu);
                tc.setFileType(fileType);
                list.add(tc);
            }

            repo.save(list);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new LongDto((long) i-1);
    }

    private Function<Long, TemplateCode> findById;
    private Function<TemplateCode, TemplateCodeDto> transformToDto;
}
