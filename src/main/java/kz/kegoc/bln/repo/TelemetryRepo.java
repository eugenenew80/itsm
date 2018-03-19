package kz.kegoc.bln.repo;

import kz.kegoc.bln.entity.Telemetry;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TelemetryRepo extends CrudRepository<Telemetry, Long> {
    List<Telemetry> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
