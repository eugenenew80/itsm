package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of= {"groupCode", "statusCode", "year"})
@ApiModel(value = "Slice", description = "Срез")
@Data
@JsonPropertyOrder({ "groupCode", "statusCode", "groupName", "statusName", "year" })
public class GroupAndStatusDto {

    @ApiModelProperty(value = "Код группы", example = "01", position = 0)
    private String groupCode;

    @ApiModelProperty(value = "Наименование группы", example = "Группа отчётов 1", position = 1)
    private String groupName;

    @ApiModelProperty(value = "Код статуса", example = "1", position = 2)
    private String statusCode;

    @ApiModelProperty(value = "Наименование статуса + год", example = "Статус 1 2019", position = 3)
    private String statusName;

    @ApiModelProperty(value = "Год", example = "2019", position = 4)
    private Integer year;

    @ApiModelProperty(value = "Срезы", example = "", position = 5)
    private List<SliceDto> children = new ArrayList<>();
}
