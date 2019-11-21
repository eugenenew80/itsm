package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of= {"statusCode", "year"})
@ApiModel(value = "Slice", description = "Срез")
@Data
@JsonPropertyOrder({ "statusCode", "statusName", "year", "children" })
public class StatusSliceDto {

    @ApiModelProperty(value = "Код статуса", example = "1", position = 0)
    private String statusCode;

    @ApiModelProperty(value = "Наименование статуса + год", example = "Статус 1 2019", position = 1)
    private String statusName;

    @ApiModelProperty(value = "Год", example = "2019", position = 2)
    private Integer year;

    @ApiModelProperty(value = "Срезы", example = "", position = 3)
    private List<SliceDto> children = new ArrayList<>();
}
