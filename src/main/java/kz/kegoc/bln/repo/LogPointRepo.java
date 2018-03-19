package kz.kegoc.bln.repo;

import kz.kegoc.bln.entity.LogPoint;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LogPointRepo extends CrudRepository<LogPoint, Long> {
    List<LogPoint> findAll();
}
