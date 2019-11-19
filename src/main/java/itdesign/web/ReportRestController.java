package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.Report;
import itdesign.entity.ReportCode;
import itdesign.entity.SheetCode;
import itdesign.entity.TemplateCode;
import itdesign.repo.ReportCodeRepo;
import itdesign.repo.ReportRepo;
import itdesign.repo.SheetCodeRepo;
import itdesign.repo.TemplateCodeRepo;
import itdesign.web.dto.CreateReportDto;
import itdesign.web.dto.ReportCodeDto2;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dozer.DozerBeanMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(tags = "API для работы с отчётами")
@RestController
@RequiredArgsConstructor
public class ReportRestController extends BaseController {
    private final TemplateCodeRepo templateCodeRepo;
    private final ReportCodeRepo reportCodeRepo;
    private final SheetCodeRepo sheetCodeRepo;
    private final ReportRepo reportRepo;
    private final DozerBeanMapper mapper;
    private final EntityManager em;

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
            .map(t -> t.getReportCode().substring(0,3))
            .collect(Collectors.toList());

        if (reportCodes.size() == 0)
            return Collections.emptyList();

        return reportCodeRepo.findByCodesAndLang(reportCodes, lang.toUpperCase())
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @ApiOperation(value="Сформировать отчёт")
    @PostMapping(value = "/api/v1/{lang}/slices/reports/download")
    public ResponseEntity<Resource> download(
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang,
        @RequestBody CreateReportDto dto
    ) {

        Report report = reportRepo.findAllBySliceId(dto.getSliceId())
            .stream()
            .filter(t -> t.getReportCode().startsWith(dto.getReportCode()))
            .findFirst()
            .orElse(null);

        if (report == null)
            return null;

        TemplateCode template = templateCodeRepo.findByCodeAndLang(report.getTemplateCode(), lang);
        if (template == null)
            return null;


        Query query = em.createNativeQuery("select d_divtbl, d_row, d_col, SUM(d_summ) AS d_summ from slice." + report.getTableData() + " group by d_divtbl, d_row, d_col order by d_divtbl, d_row, d_col");
        List<Object[]> resultList = query.getResultList();


        try (InputStream excelFileToRead = new ByteArrayInputStream(template.getBinaryFile())) {
            Workbook workbook = new XSSFWorkbook(excelFileToRead);

            for (Object[] obj : resultList) {
                String codeSheet = obj[0].toString();
                SheetCode sheetCode = sheetCodeRepo.findByCodeAndReportCodeAndLang(codeSheet, report.getReportCode(), lang);

                Sheet sheet = workbook.getSheet(sheetCode.getName());

                int rowIndex = (short)obj[1] + report.getStartRow() - 2;
                int colIndex = (short)obj[2] + report.getStartColumn() - 2;
                Number val = (Number) obj[2];

                logger.trace( "sheetCode=" + sheetCode.getCode()  + ", sheet=" + sheetCode.getName()  + ", row=" + rowIndex + ", col=" + colIndex + ", val=" + val);

                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                Cell cell = row.getCell(colIndex);
                if (cell == null) {
                    continue;
                }

                if (val == null) {
                    continue;
                }

                cell.setCellValue(val.doubleValue());
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            ByteArrayResource resource = new ByteArrayResource(bos.toByteArray());
            bos.close();

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
