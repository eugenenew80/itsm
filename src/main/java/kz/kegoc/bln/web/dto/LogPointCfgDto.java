package kz.kegoc.bln.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"logTi"})
public class LogPointCfgDto {
	private Long logPointId;
	private LocalDateTime start;
	private LocalDateTime end;
}
