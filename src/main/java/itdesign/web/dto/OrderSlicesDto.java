package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Data
public class OrderSlicesDto {
    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate startDate;

    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate endDate;

    private Long maxRecNum;
    private String region;
    private List<Long> groups;

    public List<OrderSliceDto> list() {
        return groups.stream()
            .map(groupId -> {
                OrderSliceDto t = new OrderSliceDto();
                t.setStartDate(getStartDate());
                t.setEndDate(getEndDate());
                t.setMaxRecNum(getMaxRecNum());
                t.setRegion(getRegion());
                t.setGroupId(groupId);
                return t;
            })
            .collect(toList());
    }
}
