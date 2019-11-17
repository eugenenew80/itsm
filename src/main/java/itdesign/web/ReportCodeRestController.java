package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.ReportCode;
import itdesign.repo.ReportCodeRepo;
import itdesign.web.dto.LongDto;
import itdesign.web.dto.ReportCodeDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dozer.DozerBeanMapper;
import org.springframework.core.io.ClassPathResource;
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

    @ApiOperation(value="Импорт данных из файла Excel")
    @PostMapping(value = "/api/v1/{lang}/slices/reportCodes/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importData(@PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang) {
        long count = repo.count();
        if (count > 0)
            return new LongDto(count);

        int i = 0;
        try (InputStream ExcelFileToRead = new FileInputStream(new ClassPathResource("rCodeRep.xlsx").getFile())) {
            Workbook workbook = new XSSFWorkbook(ExcelFileToRead);
            Sheet sheet = workbook.getSheetAt(0);

            List<ReportCode> list = new ArrayList<>();
            for (Row row : sheet) {
                i++;
                if (i == 1) continue;
                int j = 0;
                String nameRu = "";
                String nameKz = "";
                String code = "";
                for (Cell cell : row) {
                    j++;
                    if (j == 1)
                        nameRu = cell.getStringCellValue();
                    if (j == 2)
                        nameKz = cell.getStringCellValue();
                    if (j == 3)
                        code = cell.getStringCellValue();
                    if (j > 3)
                        continue;
                }
                if (code == null || code.isEmpty())
                    continue;

                ReportCode rc = new ReportCode();
                rc.setCode(code);
                rc.setLang("RU");
                rc.setName(nameRu);
                list.add(rc);

                rc = new ReportCode();
                rc.setCode(code);
                rc.setLang("KZ");
                rc.setName(nameKz);
                list.add(rc);
            }

            repo.save(list);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new LongDto((long) i-1);
    }

    private Function<Long, ReportCode> findById;
    private Function<ReportCode, ReportCodeDto> transformToDto;
}
