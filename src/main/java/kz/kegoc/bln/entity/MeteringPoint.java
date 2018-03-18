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
    private Long id;

    @Column
    private String name;
}
