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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(tags = "API для работы с отчётами")
@RestController
@RequiredArgsConstructor
public class ReportRestController extends BaseController {
    private static final String className = ReportRestController.class.getName();
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
        logger.debug(className + ".getAll()");
        logger.trace("sliceId: " + sliceId);
        logger.trace("lang: " + lang);

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
        logger.debug(className + ".download()");
        logger.trace("lang: " + lang);

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

        String sql = "select d_divtbl, d_row, d_col, sum(d_summ) as d_summ from slice." + report.getTableData() + " group by d_divtbl, d_row, d_col order by d_divtbl, d_row, d_col";
        Query query = em.createNativeQuery(sql);
        List<Object[]> resultList = query.getResultList();

        Map<String, SheetCode> mapSheetTemplates = new HashMap<>();
        try (InputStream templateInputStream = new ByteArrayInputStream(template.getBinaryFile())) {
            Workbook workbook = new XSSFWorkbook(templateInputStream);

            for (Object[] objRow : resultList) {
                String sheetCode = objRow[0].toString();
                int rowIndex = (short)objRow[1] + report.getStartRow() - 2;
                int colIndex = (short)objRow[2] + report.getStartColumn() - 2;
                Number val = (Number) objRow[3];

                SheetCode sheetTemplate = mapSheetTemplates.getOrDefault(
                    sheetCode,
                    sheetCodeRepo.findByCodeAndReportCodeAndLang(sheetCode, report.getReportCode(), lang)
                );

                if (sheetTemplate == null)
                    continue;

                mapSheetTemplates.putIfAbsent(sheetCode, sheetTemplate);

                //Получаем ссылку лист
                Sheet sheet = workbook.getSheet(sheetTemplate.getName());

                //Получаем ссылку на строку
                Row row = sheet.getRow(rowIndex);
                if (row == null)
                    continue;

                //Получаем ссылку на ячейку
                Cell cell = row.getCell(colIndex);
                if (cell == null)
                    continue;

                //Вывордим значение в ячейку
                if (val != null)
                    cell.setCellValue(val.doubleValue());
            }

            return buildResponse(template.getName(), workbook);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ResponseEntity<Resource> buildResponse(String templateName, Workbook workbook) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        ByteArrayResource resource = new ByteArrayResource(bos.toByteArray());
        bos.close();

        return ResponseEntity.ok()
            .header("Content-disposition", "attachment; filename=" + templateName)
            .contentLength(resource.contentLength())
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
            .body(resource);
    }

    private Function<ReportCode, ReportCodeDto2> transformToDto;
}
