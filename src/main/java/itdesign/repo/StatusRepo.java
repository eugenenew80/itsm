package itdesign.repo;

import itdesign.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepo extends JpaRepository<Status, Long> {
}
