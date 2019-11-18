package itdesign.repo;

import itdesign.entity.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface SliceRepo extends JpaRepository<Slice, Long> {
    List<Slice> findAllByGroupCodeAndStatusCode(String groupCode, String statusCode);
}
