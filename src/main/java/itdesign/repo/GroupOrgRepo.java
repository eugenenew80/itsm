package itdesign.repo;

import itdesign.entity.GroupOrg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface GroupOrgRepo extends JpaRepository<GroupOrg, GroupOrg> {
    List<GroupOrg> findAllByGroupCode(String groupCode);
}
