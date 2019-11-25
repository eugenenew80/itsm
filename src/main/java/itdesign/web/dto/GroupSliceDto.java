package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of= {"code"})
@ApiModel(value = "Slice", description = "Срез")
@Data
@JsonPropertyOrder({ "code", "name", "children" })
public class GroupSliceDto {

    @ApiModelProperty(value = "Код группы", example = "01", position = 0)
    private String code;

    @ApiModelProperty(value = "Наименование группы", example = "Группа отчётов 1", position = 1)
    private String name;

    @ApiModelProperty(value = "Срезы", example = "", position = 2)
    private List<StatusSliceDto> children = new ArrayList<>();
}
