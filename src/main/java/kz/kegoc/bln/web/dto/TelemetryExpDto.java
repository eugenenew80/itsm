package kz.kegoc.bln.web.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TelemetryExpDto {
    private String systemCode;
    private Long logPointId;
    private LocalDateTime dateTime;
    private Double val;
}
