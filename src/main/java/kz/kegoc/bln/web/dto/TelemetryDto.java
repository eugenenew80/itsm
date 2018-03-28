package kz.kegoc.bln.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
public class TelemetryDto {
    private Long id;
    private String systemCode;
    private LogPointRelDto logPoint;
    private LocalDateTime dateTime;
    private Double val;
}
