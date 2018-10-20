package bln.repo;

import bln.entity.MissingTelemetry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissingTelemetryRepo extends JpaRepository<MissingTelemetry, Long> { }
