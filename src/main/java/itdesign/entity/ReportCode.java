package itdesign.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@EqualsAndHashCode(of= {"id"})
@NoArgsConstructor
@Entity
@Table(name = "report_codes")
public class ReportCode {

    @Id
    @SequenceGenerator(name="report_codes_s", sequenceName = "report_codes_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_codes_s")
    private Long id;

    @Column
    private String code;

    @Column
    private String lang;

    @Column
    private String name;

    @Column
    private String templateCode;
}
