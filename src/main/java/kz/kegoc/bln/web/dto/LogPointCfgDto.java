package kz.kegoc.bln.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"logTi"})
public class LogPointCfgDto {
	private Long logTi;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
}
