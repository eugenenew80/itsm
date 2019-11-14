package itdesign.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Region", description = "Регионы")
@Data
public class RegionDto {

    @ApiModelProperty(value = "Идентификатор", example = "", position = 0)
    private Long id;

    @ApiModelProperty(value = "Код", example = "", position = 1)
    private String code;

    @ApiModelProperty(value = "Название", example = "", position = 3)
    private String name;
}
