package itdesign.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode(of= {"groupCode, reportCode"})
@NoArgsConstructor
@Entity
@Table(name = "group_reports")
@IdClass(GroupReport.class)
public class GroupReport implements Serializable {

    @Id
    @Column(name = "group_code")
    private String groupCode;

    @Id
    @Column(name = "report_code")
    private String reportCode;
}