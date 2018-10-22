package bln.repo;

import bln.entity.LogPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LogPointRepo extends JpaRepository<LogPoint, Long> {
    List<LogPoint> findAllByIsNewPoint(Boolean isActive);
}