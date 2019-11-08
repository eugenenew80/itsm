package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class NewSliceDto {
    private String startDate;
    private String endDate;
    private Long maxNumber;
    private String region;
    private Set<Long> groups;
}
