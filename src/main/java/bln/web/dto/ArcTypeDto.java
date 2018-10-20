package bln.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
public class ArcTypeDto {
    private Long id;
    private String code;
    private String name;
    private Long step;
    private LocalDateTime lastLoadTime;
}
