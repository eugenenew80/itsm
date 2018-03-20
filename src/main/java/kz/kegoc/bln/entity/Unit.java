package kz.kegoc.bln.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "units", schema = "apps")
public class Unit {
    @Id
    @SequenceGenerator(name="units_s", sequenceName = "apps.units_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "units_s")
    private Long id;

    @Column
    private String name;
}
