package itdesign.web.external;

import itdesign.entity.SheetCode;
import itdesign.repo.SheetCodeRepo;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class SheetCodeImporter implements DataImporter {
    private final SheetCodeRepo repo;

    @Override
    public Long importData(String fileName) {
        long count = repo.count();
        if (count > 0)
            repo.deleteAll();

        long i = 0;
        try (InputStream ExcelFileToRead = new FileInputStream(new ClassPathResource(fileName).getFile())) {
            Workbook workbook = new XSSFWorkbook(ExcelFileToRead);
            Sheet sheet = workbook.getSheetAt(0);

            List<SheetCode> list = new ArrayList<>();
            for (Row row : sheet) {
                i++;
                if (i == 1) continue;
                int j = 0;
                String name = "";
                String code = "";
                String reportCode = "";
                for (Cell cell : row) {
                    j++;
                    if (j == 1)
                        name = cell.getStringCellValue();
                    if (j == 3)
                        reportCode = cell.getStringCellValue();
                    if (j == 4)
                        code = cell.getStringCellValue();
                    if (j > 4) continue;
                }
                if (code == null || code.isEmpty())
                    continue;

                SheetCode sc = new SheetCode();
                sc.setCode(code);
                sc.setLang("RU");
                sc.setName(name);
                sc.setReportCode(reportCode);
                list.add(sc);

                sc = new SheetCode();
                sc.setCode(code);
                sc.setLang("KZ");
                sc.setName(name);
                sc.setReportCode(reportCode);
                list.add(sc);
            }

            repo.save(list);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return i-1;
    }
}
