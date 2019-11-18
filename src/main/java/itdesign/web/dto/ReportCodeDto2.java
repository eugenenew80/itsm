package itdesign.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Report", description = "Отчёты")
@Data
public class ReportCodeDto2 {

    @ApiModelProperty(value = "Код", example = "", position = 0)
    private String code;

    @ApiModelProperty(value = "Название", example = "", position = 1)
    private String name;
}
