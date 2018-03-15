package kz.kegoc.bln;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
public class Telemetry {
    private Long id;
    private Long meteringPointId;
    private LocalDateTime meteringDate;
    private Double val;
}
