package bln.entity;

import bln.converter.jpa.BooleanToIntConverter;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
@NoArgsConstructor
@Entity
@Table(name = "log_points")
public class LogPoint {
    public LogPoint(Long id) { this.id = id; }

    @Id
    private Long id;

    @Column
    private String name;

    @Column(name = "last_load_time")
    private LocalDateTime lastLoadTime;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "is_new_point")
    @Convert(converter = BooleanToIntConverter.class)
    private Boolean isNewPoint;
}
