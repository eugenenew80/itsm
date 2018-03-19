package kz.kegoc.bln.webapi.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of= {"id"})
public class LogPointRelDto {
    private Long id;
}
