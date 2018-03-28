package kz.kegoc.bln.web.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TelemetryExpDto {
    private String systemCode;
    private Long logPoint;
    private String unitCode;
    private String paramCode;
    private LocalDateTime dateTime;
    private Double val;
}
