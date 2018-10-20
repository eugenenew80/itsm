package bln.entity;

import bln.converter.jpa.BooleanToIntConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"code"})
@Entity
@Table(name = "arc_types")
public class ArcType {
    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column
    private Long step;

    @Column(name = "last_load_time")
    private LocalDateTime lastLoadTime;

    @Column(name = "is_active")
    @Convert(converter = BooleanToIntConverter.class)
    private Boolean isActive;

    @Column(name = "oic_arc_id")
    private Long oicArcId;
}
