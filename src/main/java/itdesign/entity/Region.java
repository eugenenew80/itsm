package itdesign.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
@EqualsAndHashCode(of= {"id"})
@NoArgsConstructor
@Entity
@Table(name = "regions")
public class Region implements HasLang {

    @Id
    @SequenceGenerator(name="regions_s", sequenceName = "regions_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "regions_s")
    private Long id;

    @Column
    private String code;

    @Column
    private String name;

    @Column
    private String lang;

    @Column(name = "reg_type")
    private String regType;
}