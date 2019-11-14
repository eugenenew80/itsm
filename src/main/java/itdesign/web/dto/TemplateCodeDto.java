package itdesign.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "TemplateCode", description = "Код шаблона")
@Data
public class TemplateCodeDto {
    @ApiModelProperty(value = "Идентификатор", example = "", position = 0)
    private Long id;

    @ApiModelProperty(value = "Код", example = "", position = 1)
    private String code;

    @ApiModelProperty(value = "Язык", example = "", position = 2)
    private String lang;

    @ApiModelProperty(value = "Название", example = "", position = 3)
    private String name;

    @ApiModelProperty(value = "Тип файла", example = "", position = 4)
    private String fileType;
}
