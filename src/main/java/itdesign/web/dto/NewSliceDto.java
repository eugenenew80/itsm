package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class NewSliceDto {
    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate startDate;

    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate endDate;

    private Long maxRecNum;
    private String region;
    private Set<Long> groups;
}
