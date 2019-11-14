package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.GroupOrg;
import itdesign.entity.GroupReport;
import itdesign.entity.Organization;
import itdesign.entity.ReportCode;
import itdesign.repo.GroupOrgRepo;
import itdesign.repo.GroupReportRepo;
import itdesign.repo.OrganizationRepo;
import itdesign.web.dto.LongDto;
import itdesign.web.dto.OrganizationDto;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static itdesign.util.Util.first;
import static java.util.Collections.*;

@Api(tags = "API для работы с ведомствами")
@RestController
@RequiredArgsConstructor
public class OrganizationRestController extends BaseController {
    private final OrganizationRepo repo;
    private final GroupOrgRepo groupOrgRepo;
    private final GroupReportRepo groupReportRepo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");

        findById = repo::findOne;
        transformToDto = t -> mapper.map(t, OrganizationDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/slices/orgs", produces = "application/json")
    public List<OrganizationDto> getAll(@RequestParam(value = "reportCode", defaultValue = "") @ApiParam(value = "Код отчёта", example = "001") String reportCode) {
        logger.debug(getClass().getName() + ".getAll()");

        Sort sort = new Sort(Sort.Direction.ASC, "id");

        if (reportCode == null || reportCode.isEmpty())
            return repo.findAll(sort)
                .stream()
                .map(transformToDto::apply)
                .collect(Collectors.toList());
        else {
            List<GroupReport> groupReports = groupReportRepo.findAllByReportCode(reportCode);

            return groupReports.stream()
                .map(t -> repo.findAllByGroupReport(t.getGroupCode()))
                .flatMap(t -> t.stream())
                .distinct()
                .map(transformToDto::apply)
                .collect(Collectors.toList());
        }
    }

    @ApiOperation(value="Получить запись по идентификатору")
    @GetMapping(value = "/api/v1/slices/orgs/{id}", produces = "application/json")
    public OrganizationDto getById(@PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id) {
        logger.debug(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    @ApiOperation(value="Получить список ведоиств по идентификатору согласно заданной группировки")
    @GetMapping(value = "/api/v1/slices/orgs/{id}/groupOrgs", produces = "application/json")
    public List<String> getGroupOrgs(@PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id) {
        logger.debug(getClass().getName() + ".getGroupOrgs()");

        Organization org = repo.findOne(id);
        if (org == null || org.getGroupOrg() == null)
            return emptyList();

        return groupOrgRepo.findAllByGroupCode(org.getGroupOrg())
            .stream()
            .map(t -> t.getOrgCode())
            .collect(Collectors.toList());
    }

    @ApiOperation(value="Получить список отчетов по идентификатору согласно заданной группировки")
    @GetMapping(value = "/api/v1/slices/orgs/{id}/groupReports", produces = "application/json")
    public List<String> getGroupReports(@PathVariable @ApiParam(value = "Идентификатор записи", required = true, example = "1") Long id) {
        logger.debug(getClass().getName() + ".getGroupOrgs()");

        Organization org = repo.findOne(id);
        if (org == null || org.getGroupReport() == null)
            return emptyList();

        return groupReportRepo.findAllByGroupCode(org.getGroupReport())
            .stream()
            .map(t -> t.getReportCode())
            .collect(Collectors.toList());
    }



    @ApiOperation(value="Импорт данных из файла Excel")
    @PostMapping(value = "/api/v1/slices/orgs/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importData() {
        long count = repo.count();
        if (count > 0)
            return new LongDto(count);

        Integer i = 0;
        Integer k = 0;
        Integer l = 0;
        Map<String, List<GroupOrg>> morgs = new HashMap<>();
        Map<String, List<GroupReport>> mreps = new HashMap<>();

        try (InputStream ExcelFileToRead = new FileInputStream(new ClassPathResource("Rep_ved.xlsx").getFile())) {
            Workbook workbook = new XSSFWorkbook(ExcelFileToRead);
            Sheet sheet = workbook.getSheetAt(0);

            List<Organization> orgs = new ArrayList<>();
            List<GroupReport> groupReports = new ArrayList<>();
            List<GroupOrg> groupOrgs = new ArrayList<>();
            for (Row row : sheet) {
                i++;
                if (i == 1) continue;
                int j = 0;
                String nameRu = "";
                String code = "";
                String reportCodesStr = "";
                String orgCodesStr = "";
                for (Cell cell : row) {
                    j++;
                    if (j == 1)
                        nameRu = cell.getStringCellValue();
                    if (j == 3)
                        code = cell.getStringCellValue();
                    if (j == 5)
                        reportCodesStr = cell.getStringCellValue();;
                    if (j == 9)
                        orgCodesStr = cell.getStringCellValue();;
                    if (j > 9)
                        continue;
                }
                if (code == null || code.isEmpty())
                    continue;

                String groupOrgCode = "";
                String groupReportCode= "";


                Organization o = new Organization();
                o.setCode(code);
                o.setLang("RU");
                o.setName(nameRu);

                if (reportCodesStr !=null && !reportCodesStr.isEmpty()) {
                    l++;
                    groupReportCode = l.toString();
                    if (groupReportCode.length() == 1)
                        groupReportCode = "0" + groupReportCode;
                    o.setGroupReport(groupReportCode);
                }

                if (orgCodesStr !=null && !orgCodesStr.isEmpty()) {
                    k++;
                    groupOrgCode = k.toString();
                    if (groupOrgCode.length() == 1)
                        groupOrgCode = "0" + groupOrgCode;
                    o.setGroupOrg(groupOrgCode);
                }

                orgs.add(o);

                if (reportCodesStr !=null && !reportCodesStr.isEmpty()) {
                    logger.trace(reportCodesStr);

                    String[] reportsCodes = reportCodesStr.split(",");
                    for (String  reportCode : reportsCodes) {
                        GroupReport gr = new GroupReport();
                        gr.setReportCode(reportCode);
                        gr.setGroupCode(groupReportCode);
                        groupReports.add(gr);
                    }

                    mreps.put(reportCodesStr, groupReports);
                }

                if (orgCodesStr !=null && !orgCodesStr.isEmpty()) {
                    logger.trace(orgCodesStr);

                    if (mreps.containsKey(groupReportCode)) {
                        for (GroupReport go1 : mreps.get(groupReportCode))  {
                            GroupOrg go = new GroupOrg();
                            go.setOrgCode(go.getOrgCode());
                            go.setGroupCode(go.getGroupCode());
                            groupOrgs.add(go);
                        }
                    }
                    else {
                        String[] orgCodes = orgCodesStr.split(",");
                        for (String orgCode : orgCodes) {
                            GroupOrg go = new GroupOrg();
                            go.setOrgCode(orgCode);
                            go.setGroupCode(groupReportCode);
                            groupOrgs.add(go);
                        }
                        mreps.put(reportCodesStr, groupReports);
                    }
                }
            }

            repo.save(orgs);
            groupOrgRepo.save(groupOrgs);
            groupReportRepo.save(groupReports);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new LongDto((long) i-1);
    }


    private Function<Long, Organization> findById;
    private Function<Organization, OrganizationDto> transformToDto;
}
