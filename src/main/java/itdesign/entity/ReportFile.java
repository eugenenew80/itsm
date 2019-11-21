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
@Table(name = "report_files")
@EntityListeners(PreventRemove.class)
public class ReportFile {

    @Id
    @SequenceGenerator(name="report_files_ы", sequenceName = "report_files_ы", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_files_ы")
    private Long id;

    @Column(name = "report_code")
    private String reportCode;

    @Column(name = "reg_code")
    private String regCode;

    @Column(name = "org_code")
    private String orgCode;

    @Column(name = "slice_id")
    private Long sliceId;

    @Column(name = "name")
    private String name;

    @Column(name = "file_type")
    private String fileType;

    @Column
    private String lang;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "binary_file", columnDefinition = "BLOB")
    private byte[] binaryFile;
}
