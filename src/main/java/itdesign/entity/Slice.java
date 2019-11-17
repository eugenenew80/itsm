package itdesign.entity;

import itdesign.entity.util.PreventRemove;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(of= {"id"})
@NoArgsConstructor
@Entity
@Table(name = "slices")
@EntityListeners(PreventRemove.class)
public class Slice implements HasLang {

    @Id
    @SequenceGenerator(name="slices_s", sequenceName = "slices_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slices_s")
    private Long id;

    @Column(name = "group_code")
    private String groupCode;

    @Column(name = "status_code")
    private String statusCode;

    @Column
    private String region;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "max_rec_num")
    private  Long maxRecNum;

    @Column(name = "err_msg")
    private String err_msg;

    @Transient
    private Group group;

    @Transient
    private Status status;

    @Transient
    private String lang;

    @Transient
    public String getFullStatus() {
        String fullStatus = getStatus() != null ? getStatus().getName() : "";
        fullStatus = fullStatus + (startDate != null ? " " + startDate.getYear() : "");
        return fullStatus;
    }

    @Transient
    public String getPeriod() {
        String period = startDate!= null ? startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "";
        period = period + (endDate!= null ?  " - " +endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "");
        return period;
    }
}
