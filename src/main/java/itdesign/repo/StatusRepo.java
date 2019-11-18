package itdesign.repo;

import itdesign.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface StatusRepo extends JpaRepository<Status, Long> {
    Status findByCodeAndLang(String code, String lang);
    List<Status> findAllByLang(String lang);
}
