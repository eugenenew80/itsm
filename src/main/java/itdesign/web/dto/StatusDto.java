package itdesign.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of= {"id"})
public class StatusDto {
    private Long id;
    private String name;
}
