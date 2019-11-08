package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderSliceDto {
    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate startDate;

    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate endDate;

    private Long maxRecNum;
    private String region;
    private Long groupId;
}
