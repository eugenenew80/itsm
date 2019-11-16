package itdesign.repo;

import itdesign.entity.TemplateCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateCodeRepo extends JpaRepository<TemplateCode, Long> {
    TemplateCode findByCodeAndLang(String code, String lang);
}
