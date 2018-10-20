package bln.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "missing_telemetry")
public class MissingTelemetry {
    @Id
    @SequenceGenerator(name="missing_telemetry_s", sequenceName = "missing_telemetry_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "missing_telemetry_s")
    private Long id;

    @Column(name="system_code")
    private String systemCode;

    @Column(name="arc_type_code")
    private String arcTypeCode;

    @Column(name = "date_time")
    private LocalDateTime dateTime;
}
