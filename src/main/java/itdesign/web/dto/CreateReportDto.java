package itdesign.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "CreateReport", description = "Формирование отчёта")
@Data
public class CreateReportDto {

    @ApiModelProperty(value = "Идентификатор среза", example = "7", position = 0)
    private Long sliceId;

    @ApiModelProperty(value = "Код отчёта", example = "501", position = 1)
    private String reportCode;

    @ApiModelProperty(value = "Код ведомства", example = "00", position = 2)
    private String orgCode;

    @ApiModelProperty(value = "Код региона", example = "19", position = 3)
    private String regCode;

}
