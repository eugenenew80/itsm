package itdesign.entity;

import itdesign.entity.util.PreventRemove;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
@EqualsAndHashCode(of= {"id"})
@NoArgsConstructor
@Entity
@Table(name = "slices")
@EntityListeners(PreventRemove.class)

@NamedEntityGraph(name="Slice.allJoins", attributeNodes = {
    @NamedAttributeNode("slice")
})
public class Report {

    @Id
    @SequenceGenerator(name="reports_s", sequenceName = "reports_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reports_s")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "slice_id")
    private Slice slice;

    @Column(name = "template_code")
    private String templateCode;

    @Column(name = "report_code")
    private String reportCode;

    @Column(name = "table_data")
    private String tableData;

    @Column(name = "start_row")
    private String startRow;

    @Column(name = "start_column")
    private String startColumn;
}
