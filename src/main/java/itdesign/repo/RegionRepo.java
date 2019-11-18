package itdesign.repo;

import itdesign.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface RegionRepo extends JpaRepository<Region, Long> {
    Region findByCodeAndLang(String code, String lang);
    List<Region> findAllByLang(String lang);
}

