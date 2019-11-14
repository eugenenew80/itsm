package itdesign.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "RegionTree", description = "Регионы в виде дерева")
@Data
public class RegionTreeDto {

    @ApiModelProperty(value = "Код", example = "", position = 1)
    private String code;

    @ApiModelProperty(value = "Название", example = "", position = 2)
    private String name;

    @ApiModelProperty(value = "Дочерние регионы", example = "", position = 3)
    private List<RegionTreeDto> children;
}
