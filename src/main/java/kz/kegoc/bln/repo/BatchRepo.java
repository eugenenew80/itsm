package kz.kegoc.bln.repo;

import kz.kegoc.bln.entity.Batch;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface BatchRepo extends CrudRepository<Batch, Long> {
    List<Batch> findAll();
}
