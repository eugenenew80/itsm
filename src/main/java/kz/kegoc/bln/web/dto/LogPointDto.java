package kz.kegoc.bln.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of= {"id"})
public class LogPointDto {
    private Long id;
    private String name;
    private UnitRelDto unit;
    private Boolean isActive;
}
