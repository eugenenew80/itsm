package kz.kegoc.bln.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"arc_type"})
@Entity
@Table(name = "last_load_info", schema = "apps")
public class LastLoadInfo {
    @Id
    private String arcType;

    @Column
    private Long step;

    @Column
    private LocalDateTime lastDateTime;
}
