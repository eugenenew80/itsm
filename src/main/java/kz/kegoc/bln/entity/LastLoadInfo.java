package kz.kegoc.bln.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"arcType"})
@Entity
@Table(name = "last_load_info", schema = "apps")
public class LastLoadInfo {
    @Id
    @Column(name = "arc_type")
    private String arcType;

    @Column
    private Long step;

    @Column(name = "last_load_time")
    private LocalDateTime lastLoadTime;
}
