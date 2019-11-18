package itdesign.repo;

import itdesign.entity.GroupOrg;
import itdesign.entity.GroupReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface GroupReportRepo extends JpaRepository<GroupReport, GroupOrg> {
    List<GroupReport> findAllByGroupCode(String groupCode);
    List<GroupReport> findAllByReportCode(String reportCode);
}
