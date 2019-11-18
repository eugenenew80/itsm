package itdesign.repo;

import itdesign.entity.TemplateCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface TemplateCodeRepo extends JpaRepository<TemplateCode, Long> {
    TemplateCode findByCodeAndLang(String code, String lang);
    List<TemplateCode> findAllByLang(String lang);
}
