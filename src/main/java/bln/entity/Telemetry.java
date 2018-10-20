package bln.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "telemetry")
public class Telemetry {

    @Id
    @SequenceGenerator(name="telemetry_s", sequenceName = "telemetry_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "telemetry_s")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "log_point_id")
    private LogPoint logPoint;

    @Column(name="system_code")
    private String systemCode;

    @Column(name="arc_type_code")
    private String arcTypeCode;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column
    private Double val;
}
