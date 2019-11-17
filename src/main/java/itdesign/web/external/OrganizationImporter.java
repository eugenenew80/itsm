package itdesign.web.external;

import itdesign.entity.GroupOrg;
import itdesign.entity.GroupReport;
import itdesign.entity.Organization;
import itdesign.repo.GroupOrgRepo;
import itdesign.repo.GroupReportRepo;
import itdesign.repo.OrganizationRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrganizationImporter implements DataImporter {
    private final OrganizationRepo repo;
    private final GroupOrgRepo groupOrgRepo;
    private final GroupReportRepo groupReportRepo;

    @Override
    public Long importData(String fileName) {
        long count = repo.count();
        if (count > 0) {
            repo.deleteAll();
            groupOrgRepo.deleteAll();
            groupReportRepo.deleteAll();
        }

        long i = 0;
        Integer k = 0;
        Integer l = 0;
        Map<String, String> morgs = new HashMap<>();
        Map<String, String> mreps = new HashMap<>();

        try (InputStream ExcelFileToRead = new FileInputStream(new ClassPathResource(fileName).getFile())) {
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
                String nameKz = "";
                String code = "";
                String reportCodesStr = "";
                String orgCodesStr = "";
                for (Cell cell : row) {
                    j++;
                    if (j == 1) nameRu = cell.getStringCellValue();
                    if (j == 2) nameKz = cell.getStringCellValue();
                    if (j == 3) code = cell.getStringCellValue();
                    if (j == 5) reportCodesStr = cell.getStringCellValue();;
                    if (j == 9) orgCodesStr = cell.getStringCellValue();;
                    if (j > 9) continue;
                }
                if (code == null || code.isEmpty())
                    continue;

                String groupOrgCode = "";
                String groupReportCode= "";

                Organization o = new Organization();
                o.setCode(code);
                o.setLang("RU");
                o.setName(nameRu);

                if (reportCodesStr !=null && !reportCodesStr.isEmpty() ) {
                    groupReportCode = mreps.getOrDefault(reportCodesStr, null);
                    if (groupReportCode == null) {
                        l++;
                        groupReportCode = l.toString();
                        if (groupReportCode.length() == 1) groupReportCode = "0" + groupReportCode;

                    }
                    o.setGroupReport(groupReportCode);
                }

                if (orgCodesStr !=null && !orgCodesStr.isEmpty()) {
                    groupOrgCode = morgs.getOrDefault(orgCodesStr, null);
                    if (groupOrgCode == null) {
                        k++;
                        groupOrgCode = k.toString();
                        if (groupOrgCode.length() == 1) groupOrgCode = "0" + groupOrgCode;
                    }
                    o.setGroupOrg(groupOrgCode);
                }
                orgs.add(o);

                //на казахском языке
                o = new Organization();
                o.setCode(code);
                o.setLang("KZ");
                o.setName(nameKz);
                if (groupOrgCode != null && !groupOrgCode.isEmpty())
                    o.setGroupOrg(groupOrgCode);
                if (groupReportCode != null && !groupReportCode.isEmpty())
                    o.setGroupReport(groupReportCode);
                orgs.add(o);

                //Группировка отчётов
                if (reportCodesStr !=null && !reportCodesStr.isEmpty() && !mreps.containsKey(reportCodesStr) ) {
                    String[] reportsCodesArr = reportCodesStr.split(",");;

                    for (String reportCode : reportsCodesArr) {
                        GroupReport gr = new GroupReport();
                        gr.setReportCode(reportCode);
                        gr.setGroupCode(groupReportCode);
                        groupReports.add(gr);
                    }
                }
                mreps.putIfAbsent(reportCodesStr, groupReportCode);


                //Группировка ведомств
                if (orgCodesStr !=null && !orgCodesStr.isEmpty() && !morgs.containsKey(orgCodesStr)) {
                    String[] orgCodes = orgCodesStr.split(",");
                    for (String orgCode : orgCodes) {
                        GroupOrg go = new GroupOrg();
                        go.setOrgCode(orgCode);
                        go.setGroupCode(groupReportCode);
                        groupOrgs.add(go);
                    }
                }
                morgs.putIfAbsent(orgCodesStr, groupOrgCode);
            }

            repo.save(orgs);
            groupOrgRepo.save(groupOrgs);
            groupReportRepo.save(groupReports);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return i-1;
    }
}
