package itdesign.repo;

import itdesign.entity.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SliceRepo extends JpaRepository<Slice, Long> {
    List<Slice> findAllByGroupCodeAndStatusCode(String groupCode, String statusCode);
}
