package itdesign.entity;

import itdesign.entity.util.PreventAnyUpdate;
import itdesign.entity.util.PreventUpdateAndRemove;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import javax.persistence.*;

@Data
@EqualsAndHashCode(of= {"id"})
@NoArgsConstructor
@Entity
@Table(name = "rep_groups")
@Immutable
@EntityListeners(PreventUpdateAndRemove.class)
public class Group  implements HasLang {

    @Id
    @SequenceGenerator(name="rep_groups_s", sequenceName = "rep_groups_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rep_groups_s")
    private Long id;

    @Column
    private String code;

    @Column
    private String name;

    @Column
    private String lang;
}
