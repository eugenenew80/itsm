package itdesign.web.external;

import itdesign.entity.Region;
import itdesign.repo.RegionRepo;
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
public class RegionImporter implements DataImporter {
    private final RegionRepo repo;

    @Override
    public Long importData(String fileName) {
        long count = repo.count();
        if (count > 0)
            repo.deleteAll();

        long i = 0;
        try (InputStream ExcelFileToRead = new FileInputStream(new ClassPathResource(fileName).getFile())) {
            Workbook workbook = new XSSFWorkbook(ExcelFileToRead);
            Sheet sheet = workbook.getSheetAt(0);

            List<Region> regs = new ArrayList<>();
            for (Row row : sheet) {
                i++;
                if (i == 1) continue;
                int j = 0;
                String nameRu = "";
                String nameKz = "";
                String code = "";
                String regType = "";
                for (Cell cell : row) {
                    j++;
                    if (j == 1) nameRu = cell.getStringCellValue();
                    if (j == 2) nameKz = cell.getStringCellValue();
                    if (j == 3) code = cell.getStringCellValue();
                    if (j == 4) {
                        regType = cell.getStringCellValue();
                        if (regType.equals("RESP")) regType = "00";
                        if (regType.equals("OBL")) regType = "01";
                        if (regType.equals("F10R04P1")) regType = "02";
                    }
                    if (j > 4) continue;
                }
                if (code == null || code.isEmpty())
                    continue;

                //на русском языке
                Region r = new Region();
                r.setCode(code);
                r.setLang("RU");
                r.setName(nameRu);
                r.setRegType(regType);
                regs.add(r);

                //на казахском языке
                r = new Region();
                r.setCode(code);
                r.setLang("KZ");
                r.setName(nameKz);
                r.setRegType(regType);
                regs.add(r);
            }
            repo.save(regs);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return i-1;
    }
}
