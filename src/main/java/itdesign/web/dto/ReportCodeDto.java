package itdesign.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "ReportCode", description = "Код отчёта")
@Data
public class ReportCodeDto {

    @ApiModelProperty(value = "Код", example = "", position = 0)
    private String code;

    @ApiModelProperty(value = "Язык", example = "", position = 1)
    private String lang;

    @ApiModelProperty(value = "Название", example = "", position = 2)
    private String name;

    @ApiModelProperty(value = "Код шаблона", example = "", position = 3)
    private String templateCode;
}
