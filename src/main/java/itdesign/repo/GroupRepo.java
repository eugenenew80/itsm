package itdesign.repo;

import itdesign.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface GroupRepo extends JpaRepository<Group, Long> {
    Group findByCodeAndLang(String code, String lang);
    List<Group> findAllByLang(String lang);
}
