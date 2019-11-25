package itdesign.web;

import com.google.common.base.Strings;
import io.swagger.annotations.*;
import itdesign.entity.GroupReport;
import itdesign.entity.Organization;
import itdesign.repo.GroupReportRepo;
import itdesign.repo.OrganizationRepo;
import itdesign.web.dto.OrganizationDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.http.ResponseEntity;
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
    private final GroupReportRepo groupReportRepo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        transformToDto = t -> mapper.map(t, OrganizationDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @ApiImplicitParams(
        @ApiImplicitParam(name = "sessionKey", value = "Ключ сессии", paramType = "header", dataTypeClass = String.class, example = "admin")
    )
    @GetMapping(value = "/api/v1/{lang}/slices/orgs", produces = "application/json")
    public ResponseEntity<List<OrganizationDto>> getAll(
        @RequestParam(value = "reportCode", defaultValue = "") @ApiParam(value = "Код отчёта", example = "001") String reportCode,
        @PathVariable(value = "lang") @ApiParam(value = "Язык",  example = "RU") String lang
    ) {

        List<OrganizationDto> list;
        if (Strings.isNullOrEmpty(reportCode)) {
            list = repo.findAllByLang(lang)
                .stream()
                .filter(t -> t.getLang().equals(lang.toUpperCase()))
                .map(transformToDto::apply)
                .collect(Collectors.toList());
        }
        else {
            List<GroupReport> groupReports = groupReportRepo.findAllByReportCode(reportCode);
            list = groupReports.stream()
                .map(t -> repo.findAllByGroupReportAndLang(t.getGroupCode(), lang.toUpperCase()))
                .flatMap(t -> t.stream())
                .distinct()
                .map(transformToDto::apply)
                .collect(Collectors.toList());
        }

        return ResponseEntity.ok(list);
    }

    private Function<Organization, OrganizationDto> transformToDto;
}
