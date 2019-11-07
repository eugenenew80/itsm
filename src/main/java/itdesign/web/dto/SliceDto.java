package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
@JsonPropertyOrder({ "groupName", "statusName", "sliceNumber", "period", "sliceRegion", "dbNumber", "formed" })
public class SliceDto {
    private Long sliceNumber;
    private String groupName;
    private String statusName;
    private String period;
    private Long dbNumber;
    private String sliceRegion;

    @JsonFormat(pattern="dd.MM.yyyy HH:mm:ss")
    private LocalDateTime formed;
}
