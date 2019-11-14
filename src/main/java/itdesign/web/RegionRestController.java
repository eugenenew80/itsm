package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.Region;
import itdesign.repo.RegionRepo;
import itdesign.web.dto.LongDto;
import itdesign.web.dto.RegionDto;
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


@Api(tags = "API для работы с ведомствами")
@RestController
@RequiredArgsConstructor
public class RegionRestController extends BaseController {
    private final RegionRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");

        findById = repo::findOne;
        transformToDto = t -> mapper.map(t, RegionDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/slices/regs", produces = "application/json")
    public List<RegionDto> getAll() {
        logger.debug(getClass().getName() + ".getAll()");

        Sort sort = new Sort(Sort.Direction.ASC, "id");
        return repo.findAll(sort)
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }


    @ApiOperation(value="Получить запись по идентификатору")
    @GetMapping(value = "/api/v1/slices/regs/{id}", produces = "application/json")
    public RegionDto getById(@PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id) {
        logger.debug(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }


    @ApiOperation(value="Импорт данных из файла Excel")
    @PostMapping(value = "/api/v1/slices/regs/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importData() {
        long count = repo.count();
        if (count > 0)
            repo.deleteAll();

        Integer i = 0;
        try (InputStream ExcelFileToRead = new FileInputStream(new ClassPathResource("rep_raj.xlsx").getFile())) {
            Workbook workbook = new XSSFWorkbook(ExcelFileToRead);
            Sheet sheet = workbook.getSheetAt(0);

            List<Region> regs = new ArrayList<>();
            for (Row row : sheet) {
                i++;
                if (i == 1) continue;
                int j = 0;
                String nameRu = "";
                String code = "";
                String regType = "";
                for (Cell cell : row) {
                    j++;
                    if (j == 1)
                        nameRu = cell.getStringCellValue();
                    if (j == 3)
                        code = cell.getStringCellValue();
                    if (j == 4) {
                        regType = cell.getStringCellValue();
                        if (regType.equals("RESP"))
                            regType = "00";

                        if (regType.equals("OBL"))
                            regType = "01";

                        if (regType.equals("F10R04P1"))
                            regType = "02";
                    }
                    if (j > 4)
                        continue;
                }
                if (code == null || code.isEmpty())
                    continue;

                Region r = new Region();
                r.setCode(code);
                r.setLang("RU");
                r.setName(nameRu);
                r.setRegType(regType);
                regs.add(r);
            }

            repo.save(regs);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return new LongDto((long) i-1);
    }

    private Function<Long, Region> findById;
    private Function<Region, RegionDto> transformToDto;
}
