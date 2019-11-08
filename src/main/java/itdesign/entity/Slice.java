package itdesign.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Optional.ofNullable;

@Data
@EqualsAndHashCode(of= {"id"})
@NoArgsConstructor
@Entity
@Table(name = "slices")
@Immutable
public class Slice {

    @Id
    @SequenceGenerator(name="slices_s", sequenceName = "slices_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slices_s")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

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

    @Transient
    public String getFullStatus() {
        String fullStatus = status != null ? status.getName() : "";
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
