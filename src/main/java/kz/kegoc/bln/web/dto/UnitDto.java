package kz.kegoc.bln.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of= {"id"})
public class UnitDto {
    private Long id;
    private String code;
    private String name;
}
