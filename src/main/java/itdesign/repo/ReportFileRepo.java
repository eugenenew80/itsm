package itdesign.repo;

import itdesign.entity.ReportFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository
@Transactional
public interface ReportFileRepo extends JpaRepository<ReportFile, Long> {

    @Query("select t from ReportFile t where t.reportCode = ?1 and t.orgCode = ?2  and t.regCode = ?3 and t.sliceId = ?4" )
    ReportFile findExisting(String reportCode, String orgCode, String regCode, Long sliceId);
}
