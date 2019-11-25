package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of= {"code", "year"})
@ApiModel(value = "Slice", description = "Срез")
@Data
@JsonPropertyOrder({ "code", "name", "statusYear", "children" })
public class StatusSliceDto {

    @ApiModelProperty(value = "Код статуса", example = "1", position = 0)
    private String code;

    @ApiModelProperty(value = "Наименование статуса + год", example = "Статус 1 2019", position = 1)
    private String name;

    @ApiModelProperty(value = "Год", example = "2019", position = 2)
    private Integer statusYear;

    @ApiModelProperty(value = "Срезы", example = "", position = 3)
    private List<SliceDto> children = new ArrayList<>();
}
