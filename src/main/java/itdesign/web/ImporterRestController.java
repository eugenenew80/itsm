package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import itdesign.web.dto.LongDto;
import itdesign.web.external.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "API для конвертации данных из файлов Excel")
@RestController
@RequiredArgsConstructor
public class ImporterRestController { //extends BaseController {
    private final GroupImporter groupImporter;
    private final StatusImporter statusImporter;
    private final OrganizationImporter organizationImporter;
    private final RegionImporter regionImporter;
    private final ReportCodeImporter reportCodeImporter;
    private final SheetCodeImporter sheetCodeImporter;
    private final TemplateCodeImporter templateCodeImporter;

    @ApiOperation(value="Импорт справочника групп из файла Excel")
    @PostMapping(value = "/api/v1/slices/groups/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importGroups() {
        Long count = groupImporter.importData("repgroup.xlsx");
        return new LongDto(count);
    }

    @ApiOperation(value="Импорт статусов из файла Excel")
    @PostMapping(value = "/api/v1/slices/statuses/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importStatuses() {
        Long count = statusImporter.importData("repstatus.xlsx");
        return new LongDto(count);
    }

    @ApiOperation(value="Импорт ведомств из файла Excel")
    @PostMapping(value = "/api/v1/slices/orgs/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importOrganizations() {
        Long count = organizationImporter.importData("Rep_ved.xlsx");
        return new LongDto(count);
    }

    @ApiOperation(value="Импорт регионов из файла Excel")
    @PostMapping(value = "/api/v1/slices/regs/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importRegions() {
        Long count = regionImporter.importData("rep_raj.xlsx");
        return new LongDto(count);
    }

    @ApiOperation(value="Импорт кодов отчетов из файла Excel")
    @PostMapping(value = "/api/v1/slices/reportCodes/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importReportCodes() {
        Long count = reportCodeImporter.importData("rCodeRep.xlsx");
        return new LongDto(count);
    }

    @ApiOperation(value="Импорт кодов листов из файла Excel")
    @PostMapping(value = "/api/v1/slices/sheetCodes/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importSheetCodes() {
        Long count = sheetCodeImporter.importData("kodsheet2.xlsx");
        return new LongDto(count);
    }

    @ApiOperation(value="Импорт шаблонов из файла Excel")
    @PostMapping(value = "/api/v1/slices/templateCodes/import", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LongDto importTemplateCodes() {
        Long count = templateCodeImporter.importData("templates/**");
        return new LongDto(count);
    }
}
