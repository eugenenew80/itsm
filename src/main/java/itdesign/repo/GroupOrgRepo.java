package itdesign.repo;

import itdesign.entity.GroupOrg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupOrgRepo extends JpaRepository<GroupOrg, GroupOrg> {
    List<GroupOrg> findAllByGroupCode(String groupCode);
}
