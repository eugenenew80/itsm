package itdesign.entity;

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
@Table(name = "rep_statuses")
@Immutable
@EntityListeners(PreventUpdateAndRemove.class)
public class Status implements HasLang {

    @Id
    @SequenceGenerator(name="rep_statuses_s", sequenceName = "rep_statuses_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rep_statuses_s")
    private Long id;

    @Column
    private String code;

    @Column
    private String name;

    @Column
    private String lang;
}
