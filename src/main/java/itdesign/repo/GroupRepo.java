package itdesign.repo;

import itdesign.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupRepo extends JpaRepository<Group, Long> {
    Group findByCodeAndLang(String code, String lang);
    List<Group> findAllByLang(String lang);
}
