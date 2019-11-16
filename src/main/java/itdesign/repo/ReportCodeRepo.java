package itdesign.repo;

import itdesign.entity.ReportCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportCodeRepo extends JpaRepository<ReportCode, Long> {
    ReportCode findByCodeAndLang(String code, String lang);
}
