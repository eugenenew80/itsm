package itdesign.repo;

import itdesign.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepo extends JpaRepository<Region, Long> {
    Region findByCodeAndLang(String code, String lang);
    List<Region> findAllByLang(String lang);
}

