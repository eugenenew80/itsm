package itdesign.entity;

import itdesign.entity.util.PreventAnyUpdate;
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
@EntityListeners(PreventAnyUpdate.class)
public class Status {

    @Id
    private Long id;

    @Column
    private String name;
}
