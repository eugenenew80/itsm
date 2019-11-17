package itdesign.web.external;

import itdesign.entity.TemplateCode;
import itdesign.repo.TemplateCodeRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.util.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TemplateCodeImporter implements DataImporter {
    private final TemplateCodeRepo repo;

    @Override
    public Long importData(String fileName) {
        long count = repo.count();
        if (count > 0)
            repo.deleteAll();

        Long i = 0l;
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
        try {
            Resource resources[] = resolver.getResources("classpath:" + fileName);
            for (Resource resource : resources) {
                i++;
                TemplateCode tc = new TemplateCode();
                tc.setName(resource.getFilename());

                int n = tc.getName().indexOf(".");
                tc.setCode(resource.getFilename().substring(0, 6));
                tc.setFileType(resource.getFilename().substring(n+1));

                String l = resource.getFilename().substring(6, 8);
                if (l.equals("_1"))
                    tc.setLang("RU");
                else
                    tc.setLang("KZ");

                tc.setBinaryFile(IOUtils.toByteArray(resource.getInputStream()));
                repo.save(tc);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return i;
    }
}
