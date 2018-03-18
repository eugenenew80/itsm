package kz.kegoc.bln.repo;

import kz.kegoc.bln.entity.Telemetry;
import org.springframework.data.repository.CrudRepository;

public interface TelemetryRepo extends CrudRepository<Telemetry, Long> {
}
