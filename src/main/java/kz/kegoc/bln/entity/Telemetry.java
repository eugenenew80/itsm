package kz.kegoc.bln.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "telemetry", schema = "apps")
public class Telemetry {

    @Id
    @SequenceGenerator(name="telemetry_s", sequenceName = "telemetry_s", schema = "apps", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "telemetry_s")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "metering_point_id")
    private MeteringPoint meteringPoint;

    @Column(name = "metering_date")
    private LocalDateTime meteringDate;

    @Column
    private Double val;
}
