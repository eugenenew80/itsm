package kz.kegoc.bln.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"logTi"})
public class LogPointCfgDto {
	private Long meteringPointId;
	private Long logPointId;
	private String paramCode;
	private String unitCode;
	private LocalDateTime start;
	private LocalDateTime end;
}
