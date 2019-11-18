package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ApiModel(value = "Slice", description = "Срез")
@Data
@JsonPropertyOrder({ "id", "groupName", "statusName", "period", "region", "groupCode", "statusCode", "startDate", "endDate", "maxRecNum", "created", "completed" })
public class SliceDto {
    @ApiModelProperty(value = "Идентификатор", example = "1", position = 0)
    private Long id;

    @ApiModelProperty(value = "Код группы", example = "001", position = 1)
    private String groupCode;

    @ApiModelProperty(value = "Наименование группы", example = "Группа отчётов 1", position = 2)
    private String groupName;

    @ApiModelProperty(value = "Код статуса", example = "1", position = 3)
    private String statusCode;

    @ApiModelProperty(value = "Наименование статуса + год", example = "Статус 1 2019", position = 4)
    private String statusName;

    @ApiModelProperty(value = "Период", example = "01.01.2019 - 31.01.2019", position = 5)
    private String period;

    @ApiModelProperty(value = "Максимальный номер записи", example = "9999", position = 6)
    private Long maxRecNum;

    @ApiModelProperty(value = "Регион", example = "19", position = 7)
    private String region;

    @ApiModelProperty(value = "Даата начала", example = "01.01.2019", position = 8)
    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate startDate;

    @ApiModelProperty(value = "Дата окончания", example = "31.01.2019", position = 9)
    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate endDate;

    @ApiModelProperty(value = "Дата, когда срез был заказан", example = "31.01.2019 17:30:00", position = 10)
    @JsonFormat(pattern="dd.MM.yyyy HH:mm:ss")
    private LocalDateTime created;

    @ApiModelProperty(value = "Дата, когда срез был сформирован", example = "31.01.2019 17:30:00", position = 11)
    @JsonFormat(pattern="dd.MM.yyyy HH:mm:ss")
    private LocalDateTime completed;

    @ApiModelProperty(value = "Год", example = "2019", position = 12)
    private Integer year;
}
