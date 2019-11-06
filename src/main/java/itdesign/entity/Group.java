package itdesign.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(of= {"id"})
@NoArgsConstructor
@Entity
@Table(name = "rep_groups")
@Immutable
public class Group {
    public Group(Long id) { this.id = id; }

    @Id
    private Long id;

    @Column
    private String name;
}
