package itdesign.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Group", description = "Группа")
@Data
public class GroupDto {

    @ApiModelProperty(value = "Код", example = "01", position = 0)
    private String code;

    @ApiModelProperty(value = "Наименование", example = "Группа отчётов 1", position = 1)
    private String name;
}
