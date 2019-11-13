package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.Organization;
import itdesign.repo.GroupOrgRepo;
import itdesign.repo.GroupReportRepo;
import itdesign.repo.OrganizationRepo;
import itdesign.web.dto.OrganizationDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
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
    public List<OrganizationDto> getAll() {
        logger.debug(getClass().getName() + ".getAll()");

        Sort sort = new Sort(Sort.Direction.ASC, "id");
        return repo.findAll(sort)
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
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

    private Function<Long, Organization> findById;
    private Function<Organization, OrganizationDto> transformToDto;
}
