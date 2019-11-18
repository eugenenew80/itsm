package itdesign.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "SheetCode", description = "Код листа")
@Data
public class SheetCodeDto {

    @ApiModelProperty(value = "Код", example = "", position = 0)
    private String code;

    @ApiModelProperty(value = "Язык", example = "", position = 1)
    private String lang;

    @ApiModelProperty(value = "Название", example = "", position = 2)
    private String name;

    @ApiModelProperty(value = "Код отчёта", example = "", position = 3)
    private String reportCode;
}
