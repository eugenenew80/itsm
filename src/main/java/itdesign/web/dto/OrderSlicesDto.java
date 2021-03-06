package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import static java.util.stream.Collectors.*;

@ApiModel(value = "OrderSlice", description = "Заказ среза")
@Data
public class OrderSlicesDto {

    @ApiModelProperty(value = "Даата начала", example = "01.01.2019", position = 0)
    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate startDate;

    @ApiModelProperty(value = "Дата окончания", example = "31.01.2019", position = 1)
    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate endDate;

    @ApiModelProperty(value = "Максимальный номер записи в базе данных", example = "9999", position = 2)
    private Long maxRecNum;

    @ApiModelProperty(value = "Регион", example = "19", position = 3)
    private String region;

    @ApiModelProperty(value = "Список групп, которые необходимо включить в срез", example = "['001', '002', '003']", position = 4)
    private List<String> groups;

    public List<OrderSliceDto> list() {
        return getGroups().stream()
            .map(groupCode -> {
                OrderSliceDto t = new OrderSliceDto();
                t.setStartDate(getStartDate());
                t.setEndDate(getEndDate());
                t.setMaxRecNum(getMaxRecNum());
                t.setRegion(getRegion());
                t.setGroupCode(groupCode);
                return t;
            })
            .collect(toList());
    }
}
