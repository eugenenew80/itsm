package itdesign.repo;

import itdesign.entity.TemplateCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TemplateCodeRepo extends JpaRepository<TemplateCode, Long> {
    TemplateCode findByCodeAndLang(String code, String lang);
    List<TemplateCode> findAllByLang(String lang);
}
