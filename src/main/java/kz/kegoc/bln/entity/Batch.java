package kz.kegoc.bln.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "batches", schema = "apps")
public class Batch {
    @Id
    @SequenceGenerator(name="batches_s", sequenceName = "batches_s", schema = "apps", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "batches_s")
    private Long id;

    @Column
    private String name;
}
