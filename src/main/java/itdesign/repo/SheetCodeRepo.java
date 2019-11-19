package itdesign.repo;

import itdesign.entity.SheetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface SheetCodeRepo extends JpaRepository<SheetCode, Long> {
    SheetCode findByCodeAndReportCodeAndLang(String code, String reportCode, String lang);
    List<SheetCode> findAllByLang(String lang);
}
