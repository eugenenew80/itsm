package itdesign.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
@EqualsAndHashCode(of= {"id"})
@NoArgsConstructor
@Entity
@Table(name = "organizations")
public class Organization implements HasLang{

    @Id
    @SequenceGenerator(name="organizations_s", sequenceName = "organizations_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizations_s")
    private Long id;

    @Column
    private String code;

    @Column
    private String name;

    @Column
    private String lang;

    @Column(name = "group_org_code")
    private String groupOrg;

    @Column(name = "group_report_code")
    private String groupReport;
}