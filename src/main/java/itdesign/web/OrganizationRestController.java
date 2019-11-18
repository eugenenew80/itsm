package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.GroupReport;
import itdesign.entity.Organization;
import itdesign.repo.GroupOrgRepo;
import itdesign.repo.GroupReportRepo;
import itdesign.repo.OrganizationRepo;
import itdesign.web.dto.OrganizationDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        transformToDto = t -> mapper.map(t, OrganizationDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/{lang}/slices/orgs", produces = "application/json")
    public List<OrganizationDto> getAll(
        @RequestParam(value = "reportCode", defaultValue = "") @ApiParam(value = "Код отчёта", example = "001") String reportCode,
        @PathVariable(value = "lang") @ApiParam(value = "Язык",  example = "RU") String lang
    ) {
        logger.debug(getClass().getName() + ".getAll()");

        if (reportCode == null || reportCode.isEmpty())
            return repo.findAllByLang(lang)
                .stream()
                .filter(t -> t.getLang().equals(lang.toUpperCase()))
                .map(transformToDto::apply)
                .collect(Collectors.toList());
        else {
            List<GroupReport> groupReports = groupReportRepo.findAllByReportCode(reportCode);
            return groupReports.stream()
                .map(t -> repo.findAllByGroupReportAndLang(t.getGroupCode(), lang.toUpperCase()))
                .flatMap(t -> t.stream())
                .distinct()
                .map(transformToDto::apply)
                .collect(Collectors.toList());
        }
    }

    private Function<Organization, OrganizationDto> transformToDto;
}
