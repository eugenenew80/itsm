package kz.kegoc.bln.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of= {"id"})
public class UnitRelDto {
    private Long id;
    private String name;
}
