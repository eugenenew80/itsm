package itdesign.repo;

import itdesign.entity.ReportCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportCodeRepo extends JpaRepository<ReportCode, Long> {
    ReportCode findByCodeAndLang(String code, String lang);
    List<ReportCode> findAllByLang(String lang);
}
