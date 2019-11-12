package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.SheetCode;
import itdesign.repo.SheetCodeRepo;
import itdesign.web.dto.LongDto;
import itdesign.web.dto.SheetCodeDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dozer.DozerBeanMapper;
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
    @GetMapping(value = "/api/v1/slices/sheetCodes", produces = "application/json")
    public List<SheetCodeDto> getAll() {
        logger.debug(getClass().getName() + ".getAll()");

        Sort sort = new Sort(Sort.Direction.ASC, "id");
        return repo.findAll(sort)
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @ApiOperation(value="Получить запись по идентификатору")
    @GetMapping(value = "/api/v1/slices/sheetCodes/{id}", produces = "application/json")
    public SheetCodeDto getById(@PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id) {
        logger.debug(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    @ApiOperation(value="Импорт данных из файла Excel")
    @PostMapping(value = "/api/v1/slices/sheetCodes/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importData() {

        int i = 0;
        try (InputStream ExcelFileToRead = new FileInputStream("d:/exc/kodsheet2.xlsx")) {
            Workbook workbook = new XSSFWorkbook(ExcelFileToRead);
            Sheet sheet = workbook.getSheetAt(0);

            List<SheetCode> list = new ArrayList<>();
            for (Row row : sheet) {
                i++;
                if (i == 1) continue;
                int j = 0;
                String name = "";
                String code = "";
                String reportCode = "";
                for (Cell cell : row) {
                    j++;
                    if (j == 1)
                        name = cell.getStringCellValue();
                    if (j == 3)
                        reportCode = cell.getStringCellValue();
                    if (j == 4)
                        code = cell.getStringCellValue();
                    if (j > 4) continue;
                }
                if (code == null || code.isEmpty())
                    continue;

                SheetCode sc = new SheetCode();
                sc.setCode(code);
                sc.setLang("RU");
                sc.setName(name);
                sc.setReportCode(reportCode);
                list.add(sc);

                sc = new SheetCode();
                sc.setCode(code);
                sc.setLang("KZ");
                sc.setName(name);
                sc.setReportCode(reportCode);
                list.add(sc);
            }

            repo.save(list);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new LongDto((long) i-1);
    }

    private Function<Long, SheetCode> findById;
    private Function<SheetCode, SheetCodeDto> transformToDto;
}
