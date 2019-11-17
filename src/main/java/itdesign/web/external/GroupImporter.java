package itdesign.web.external;

import itdesign.entity.Group;
import itdesign.repo.GroupRepo;
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
public class GroupImporter implements DataImporter {
    private final GroupRepo repo;

    @Override
    public Long importData(String fileName) {
        long count = repo.count();
        if (count > 0)
            repo.deleteAll();

        long i = 0;
        try (InputStream ExcelFileToRead = new FileInputStream(new ClassPathResource(fileName).getFile())) {
            Workbook workbook = new XSSFWorkbook(ExcelFileToRead);
            Sheet sheet = workbook.getSheetAt(0);

            List<Group> list = new ArrayList<>();
            for (Row row : sheet) {
                i++;
                if (i == 1) continue;
                int j = 0;
                String nameRu = "";
                String nameKz = "";
                String code = "";
                for (Cell cell : row) {
                    j++;
                    if (j == 1)
                        nameRu = cell.getStringCellValue();
                    if (j == 2)
                        nameKz = cell.getStringCellValue();
                    if (j == 3)
                        code = cell.getStringCellValue();
                    if (j > 3)
                        continue;
                }
                if (code == null || code.isEmpty())
                    continue;

                Group rc = new Group();
                rc.setCode(code);
                rc.setLang("RU");
                rc.setName(nameRu);
                list.add(rc);

                rc = new Group();
                rc.setCode(code);
                rc.setLang("KZ");
                rc.setName(nameKz);
                list.add(rc);
            }

            repo.save(list);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return i-1;
    }
}
