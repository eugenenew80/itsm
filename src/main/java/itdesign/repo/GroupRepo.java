package itdesign.repo;

import itdesign.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepo extends JpaRepository<Group, Long> {
}
