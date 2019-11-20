package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.*;
import itdesign.repo.*;
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
import java.util.*;
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
    private final OrganizationRepo organizationRepo;
    private final GroupOrgRepo groupOrgRepo;
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
    @PostMapping(value = "/api/v1/{lang}/slices/reports/createReport")
    public ResponseEntity<Resource> createReport(
        @PathVariable(value = "lang")  @ApiParam(value = "Язык",  example = "RU")  String lang,
        @RequestBody CreateReportDto dto
    ) {
        logger.debug(className + ".createReport()");
        logger.trace("lang: " + lang);

        Workbook workbook = buildReport(dto, lang);
        String fileName = dto.getReportCode() + "_" + dto.getOrgCode() + "_" + dto.getRegCode() + "_" + dto.getSliceId() + ".xlsx";
        return buildResponse(fileName, workbook);
    }

    private Workbook buildReport(CreateReportDto dto, String lang) {
        logger.trace("buildReport()");

        Report report = reportRepo.findAllBySliceId(dto.getSliceId())
            .stream()
            .filter(t -> t.getReportCode().startsWith(dto.getReportCode()))
            .findFirst()
            .orElse(null);

        //Отчёт не найден для указанного среза и кода отчёта
        if (report == null)
            throw new RuntimeException("Report not found: sliceId: " + dto.getSliceId() + ", reportCode: " + dto.getReportCode());

        //Шаблон не найден для указанного код шаблона и языка
        TemplateCode template = templateCodeRepo.findByCodeAndLang(report.getTemplateCode(), lang);
        if (template == null)
            throw new RuntimeException("Template not found: templateCode: " + report.getTemplateCode() + ", lang: " + lang);

        //Получаем данные для отчёта
        List<Object[]> resultList = getData(report.getTableData(), dto, lang);

        //Формируем отчёт, используя шаблон
        try (InputStream templateInputStream = new ByteArrayInputStream(template.getBinaryFile())) {
            Workbook workbook = new XSSFWorkbook(templateInputStream);
            Map<String, SheetCode> mapSheetTemplates = new HashMap<>();

            for (Object[] objRow : resultList) {
                String sheetCode = objRow[0].toString();
                int rowIndex = (short)objRow[1] + report.getStartRow() - 2;
                int colIndex = (short)objRow[2] + report.getStartColumn() - 2;
                Number val = (Number) objRow[3];

                SheetCode sheetTemplate = mapSheetTemplates.get(sheetCode);
                if (sheetTemplate == null) {
                    logger.trace("Finding sheet code: " + sheetCode);
                    sheetTemplate = sheetCodeRepo.findByCodeAndReportCodeAndLang(sheetCode, report.getReportCode(), lang);
                    mapSheetTemplates.put(sheetCode, sheetTemplate);
                }

                if (sheetTemplate == null)
                    throw new RuntimeException("Sheet not found: sheetCode: " + sheetCode + ", reportCode: " + report.getReportCode() + ", lang: " + lang);

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

            //return buildResponse(template.getName(), workbook);
            return workbook;
        }

        //Обработка исключений
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<Object[]> getData(String tableName, CreateReportDto dto, String lang) {
        logger.trace("getData()");

        String regCode = dto.getRegCode() + "%";
        List<String> orgCodes = getOrgCodes(dto, lang);

        //Формируем и выполняем запрос к таблице данных
        String sql = "select d_divtbl, d_row, d_col, sum(d_summ) as d_summ from slice.#table_name# where d_organ like ?1 and d_vedomst in (?2) group by d_divtbl, d_row, d_col order by d_divtbl, d_row, d_col";
        sql = sql.replace("#table_name#", tableName);

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, regCode);
        query.setParameter(2,  orgCodes);
        List<Object[]> list = query.getResultList();
        return list;
    }

    private List<String> getOrgCodes(CreateReportDto dto, String lang) {
        logger.trace("getOrgCodes()");

        Organization org = organizationRepo.findByCodeAndLang(dto.getOrgCode(), lang);
        if (org.getGroupOrg() != null) {
            return groupOrgRepo.findAllByGroupCode(org.getGroupOrg())
                .stream()
                .map(t -> t.getOrgCode())
                .collect(Collectors.toList());
        }
        return  Arrays.asList(dto.getOrgCode());
    }

    private ResponseEntity<Resource> buildResponse(String templateName, Workbook workbook)  {
        logger.trace("buildResponse()");

        ByteArrayResource resource;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
            resource = new ByteArrayResource(bos.toByteArray());
            bos.close();

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok()
            .header("Content-disposition", "attachment; filename=" + templateName)
            .contentLength(resource.contentLength())
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
            .body(resource);
    }

    private Function<ReportCode, ReportCodeDto2> transformToDto;
}
