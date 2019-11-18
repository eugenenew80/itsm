package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.Report;
import itdesign.entity.ReportCode;
import itdesign.entity.TemplateCode;
import itdesign.repo.ReportCodeRepo;
import itdesign.repo.ReportRepo;
import itdesign.repo.TemplateCodeRepo;
import itdesign.web.dto.ReportCodeDto;
import itdesign.web.dto.ReportCodeDto2;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dozer.DozerBeanMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(tags = "API для работы с отчётами")
@RestController
@RequiredArgsConstructor
public class ReportRestController extends BaseController {
    private final TemplateCodeRepo templateCodeRepo;
    private final ReportCodeRepo repo;
    private final ReportRepo reportRepo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");
        transformToDto = t -> mapper.map(t, ReportCodeDto2.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/{lang}/slices/reports", produces = "application/json")
    public List<ReportCodeDto2> getAll(
        @PathVariable(value = "lang") @ApiParam(value = "Язык", example = "RU") String lang,
        @RequestParam(value = "sliceId") @ApiParam(value = "Идентификатор среза",  example = "1") long sliceId
    ) {
        logger.debug(getClass().getName() + ".getAll()");

        List<String> reportCodes = reportRepo.findAllBySliceId(sliceId).stream()
            .map(t -> t.getReportCode())
            .collect(Collectors.toList());

        return repo.findByCodesAndLang(reportCodes, lang.toUpperCase())
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @ApiOperation(value="Сформировать отчёт")
    @GetMapping(value = "/api/v1/{lang}/slices/reports/{id}/download")
    public ResponseEntity<Resource> download(
        @PathVariable @ApiParam(value = "Идентификатор отчёта", required = true, example = "1") Long id,
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang
    ) {
        TemplateCode template = templateCodeRepo.findAll().stream().findFirst().get();

        try (InputStream excelFileToRead = new ByteArrayInputStream(template.getBinaryFile())) {
            Workbook workbook = new XSSFWorkbook(excelFileToRead);

            /*
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0);
            Cell cell = row.getCell(0);
            cell.setCellValue("QQQQQ");
            */

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            ByteArrayResource resource = new ByteArrayResource(bos.toByteArray());

            return ResponseEntity.ok()
                .header("Content-disposition", "attachment; filename=" + template.getName())
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Function<ReportCode, ReportCodeDto2> transformToDto;
}
