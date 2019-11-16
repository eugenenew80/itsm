package itdesign.repo;

import itdesign.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepo extends JpaRepository<Region, Long> {
    Region findByCodeAndLang(String code, String lang);
}

