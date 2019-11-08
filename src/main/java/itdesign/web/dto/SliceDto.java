package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
@JsonPropertyOrder({ "id", "groupName", "statusName", "period", "region", "groupId", "statusId", "startDate", "endDate", "maxRecNum", "created", "completed" })
public class SliceDto {
    private Long id;
    private Long groupId;
    private String groupName;
    private Long statusId;
    private String statusName;
    private String period;
    private Long maxRecNum;
    private String region;

    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate startDate;

    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate endDate;

    @JsonFormat(pattern="dd.MM.yyyy HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern="dd.MM.yyyy HH:mm:ss")
    private LocalDateTime completed;
}
