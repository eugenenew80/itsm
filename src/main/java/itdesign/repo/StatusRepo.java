package itdesign.repo;

import itdesign.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepo extends JpaRepository<Status, Long> {
    Status findByCodeAndLang(String code, String lang);
    List<Status> findAllByLang(String lang);
}
