package itdesign.repo;

import itdesign.entity.ReportCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ReportCodeRepo extends JpaRepository<ReportCode, Long> {
    ReportCode findByCodeAndLang(String code, String lang);
    List<ReportCode> findAllByLang(String lang);

    @Query("select r from ReportCode  r where r.code in (?1) and lang = ?2")
    List<ReportCode> findByCodesAndLang(List<String> codes, String lang);
}
