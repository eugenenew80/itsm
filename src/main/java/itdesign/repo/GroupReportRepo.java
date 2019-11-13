package itdesign.repo;

import itdesign.entity.GroupOrg;
import itdesign.entity.GroupReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupReportRepo extends JpaRepository<GroupReport, GroupOrg> {
    List<GroupReport> findAllByGroupCode(String groupCode);
}
