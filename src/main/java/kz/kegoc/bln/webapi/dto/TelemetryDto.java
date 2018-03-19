package kz.kegoc.bln.webapi.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
public class TelemetryDto {
    private Long id;
    private LogPointRelDto meteringPoint;
    private LocalDateTime meteringDate;
    private Double val;
}
