package kz.kegoc.bln.repo;

import kz.kegoc.bln.entity.Unit;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface UnitRepo extends CrudRepository<Unit, Long> {
    List<Unit> findAll();
}
