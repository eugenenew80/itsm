package kz.kegoc.bln.repo;

import kz.kegoc.bln.entity.LogPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LogPointRepo extends JpaRepository<LogPoint, Long> { }
