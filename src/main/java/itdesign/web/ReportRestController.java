package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.*;
import itdesign.repo.*;
import itdesign.web.dto.CreateReportDto;
import itdesign.web.dto.LongDto;
import itdesign.web.dto.OrganizationDto;
import itdesign.web.dto.ReportCodeDto2;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Value;
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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;

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
    private final ReportFileRepo reportFileRepo;
    private final GroupReportRepo groupReportRepo;
    private final DozerBeanMapper mapper;
    private final EntityManager em;

    @Value( "${spring.jpa.properties.hibernate.default_schema}" )
    private String schema;

    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");
        transformToDto = t -> mapper.map(t, ReportCodeDto2.class);
        transformToDto2 = t -> mapper.map(t, OrganizationDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/{lang}/slices/reports", produces = "application/json")
    public List<ReportCodeDto2> getAll(
        @PathVariable(value = "lang") @ApiParam(value = "Язык", example = "RU") String lang,
        @RequestParam(value = "sliceId") @ApiParam(value = "Идентификатор среза",  example = "1") long sliceId,
        @RequestParam(value = "withOrgs", defaultValue = "false") @ApiParam(value = "Показать ведомства",  example = "false") boolean wthOrgs
    ) {
        logger.debug(className + ".getAll()");
        logger.trace("sliceId: " + sliceId);
        logger.trace("lang: " + lang);

        List<String> reportCodes = reportRepo.findAllBySliceId(sliceId).stream()
            .map(t -> t.getReportCode().substring(0,3))
            .collect(toList());

        if (reportCodes.size() == 0)
            return Collections.emptyList();

        List<ReportCodeDto2> listReportDto = reportCodeRepo.findByCodesAndLang(reportCodes, lang.toUpperCase())
            .stream()
            .map(transformToDto::apply)
            .collect(toList());

        if (wthOrgs) {
            for (ReportCodeDto2 repCodeDto : listReportDto) {
                List<GroupReport> groupReports = groupReportRepo.findAllByReportCode(repCodeDto.getCode());
                List<OrganizationDto> listOrgDto = groupReports.stream()
                    .map(t -> organizationRepo.findAllByGroupReportAndLang(t.getGroupCode(), lang.toUpperCase()))
                    .flatMap(t -> t.stream())
                    .distinct()
                    .map(transformToDto2::apply)
                    .collect(toList());

                repCodeDto.setOrgs(listOrgDto);
            }
        }

        return listReportDto;
    }

    @ApiOperation(value="Сформировать список отчётов")
    @PostMapping(value = "/api/v1/{lang}/slices/reports/createReports")
    public List<LongDto> createReports(
        @PathVariable(value = "lang") @ApiParam(value = "Язык",  example = "RU")  String lang,
        @RequestBody List<CreateReportDto> repListDto
    ) {
        logger.debug(className + ".createReports()");
        logger.trace("lang: " + lang);

        List<LongDto> list = new ArrayList<>();
        for (CreateReportDto dto : repListDto) {
            ReportFile rf = buildReportFile(dto, lang);
            list.add(new LongDto(rf.getId()));
        }

        return list;
    }

    @ApiOperation(value="Сформировать отчёта")
    @PostMapping(value = "/api/v1/{lang}/slices/reports/createReport")
    public LongDto createReport(
        @PathVariable(value = "lang") @ApiParam(value = "Язык",  example = "RU")  String lang,
        @RequestBody CreateReportDto dto
    ) {
        logger.debug(className + ".createReport()");
        logger.trace("lang: " + lang);

        ReportFile rf = buildReportFile(dto, lang);
        return  new LongDto(rf.getId());
    }

    @ApiOperation(value="Выгрузить отчёт")
    @GetMapping(value = "/api/v1/{lang}/slices/reports/{id}/download")
    public ResponseEntity<Resource> downloadReport(@PathVariable(value = "id") @ApiParam(value = "Идентификатор файла",  example = "1") long id) {
        logger.debug(className + ".downloadReport()");

        //Ищем файл
        ReportFile rf = reportFileRepo.findOne(id);
        if (rf == null)
            throw new RuntimeException("Report file no found, id: " + id);

        //Формируем ответ
        ByteArrayResource resource = new ByteArrayResource(rf.getBinaryFile());
        return ResponseEntity.ok()
            .header("Content-disposition", "attachment; filename=" + rf.getName())
            .contentLength(resource.contentLength())
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
            .body(resource);
    }

    private ReportFile buildReportFile(CreateReportDto dto, String lang) {
        //Выясняем, не сформирован ли уже файл
        ReportFile rf = reportFileRepo.findExisting(
            dto.getReportCode(),
            dto.getOrgCode(),
            dto.getRegCode(),
            dto.getSliceId(),
            lang
        );

        //формируем отчет
        if (rf == null) {
            Workbook workbook = null;
            try {
                workbook = buildWorkbook(dto, lang);
                String fileName = dto.getReportCode() + "_" + dto.getOrgCode() + "_" + dto.getRegCode() + "_" + dto.getSliceId() + ".xlsx";
                rf = new ReportFile();
                rf.setReportCode(dto.getReportCode());
                rf.setOrgCode(dto.getOrgCode());
                rf.setRegCode(dto.getRegCode());
                rf.setSliceId(dto.getSliceId());
                rf.setLang(lang);
                rf.setName(fileName);
                rf.setFileType("XLSX");
                rf.setBinaryFile(workbookToBytes(workbook));
                reportFileRepo.save(rf);
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            finally {
                try {
                    if (workbook != null) workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return rf;
    }

    private Workbook buildWorkbook(CreateReportDto dto, String lang) throws IOException {
        logger.trace("buildWorkbook()");

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
    }

    private List<Object[]> getData(String tableName, CreateReportDto dto, String lang) {
        logger.trace("getData()");

        String regCode = dto.getRegCode() + "%";
        List<String> orgCodes = getOrgCodes(dto, lang);

        //Формируем и выполняем запрос к таблице данных
        String sql = "select d_divtbl, d_row, d_col, sum(d_summ) as d_summ from #schema#.#table_name# where d_organ like ?1 and d_vedomst in (?2) group by d_divtbl, d_row, d_col order by d_divtbl, d_row, d_col";
        sql = sql.replace("#schema#", schema);
        sql = sql.replace("#table_name#", tableName);

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, regCode);
        query.setParameter(2,  orgCodes);
        return query.getResultList();
    }

    private List<String> getOrgCodes(CreateReportDto dto, String lang) {
        logger.trace("getOrgCodes()");

        Organization org = organizationRepo.findByCodeAndLang(dto.getOrgCode(), lang);
        if (org.getGroupOrg() != null) {
            List<String> orgCodes = groupOrgRepo.findAllByGroupCode(org.getGroupOrg())
                .stream()
                .map(t -> t.getOrgCode())
                .collect(toList());

            if (orgCodes.isEmpty())
                asList(dto.getOrgCode());

            return orgCodes;
        }
        return  asList(dto.getOrgCode());
    }

    private byte[] workbookToBytes(Workbook workbook) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos);
            return bos.toByteArray();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Function<ReportCode, ReportCodeDto2> transformToDto;

    private Function<Organization, OrganizationDto> transformToDto2;
}
