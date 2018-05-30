package kz.kegoc.bln.repo;

import kz.kegoc.bln.entity.MissingTelemetry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissingTelemetryRepo extends JpaRepository<MissingTelemetry, Long> { }
