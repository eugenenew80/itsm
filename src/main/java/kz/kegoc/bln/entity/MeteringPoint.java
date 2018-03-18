package kz.kegoc.bln.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "metering_points", schema = "apps")
public class MeteringPoint {

    @Id
    @SequenceGenerator(name="metering_points_s", sequenceName = "metering_points_s", schema = "apps", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metering_points_s")
    private Long id;

    @Column
    private String name;
}
