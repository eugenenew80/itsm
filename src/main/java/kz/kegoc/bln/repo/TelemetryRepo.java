package kz.kegoc.bln.repo;

import kz.kegoc.bln.entity.Telemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TelemetryRepo extends JpaRepository<Telemetry, Long> {
    List<Telemetry> findAllByLogPointIdAndDateTimeBetweenAndArcTypeCode(Long logPointId, LocalDateTime start, LocalDateTime end, String arcTypeCode);

    List<Telemetry> findByLogPointIdAndDateTime(Long logPointId, LocalDateTime dateTime);
}
