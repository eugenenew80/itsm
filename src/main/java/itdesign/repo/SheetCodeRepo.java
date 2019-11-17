package itdesign.repo;

import itdesign.entity.SheetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SheetCodeRepo extends JpaRepository<SheetCode, Long> {
    SheetCode findByCodeAndLang(String code, String lang);
    List<SheetCode> findAllByLang(String lang);
}
