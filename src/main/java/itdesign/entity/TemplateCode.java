package itdesign.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
@EqualsAndHashCode(of= {"id"})
@NoArgsConstructor
@Entity
@Table(name = "template_codes")
public class TemplateCode {

    @Id
    @SequenceGenerator(name="template_codes_s", sequenceName = "template_codes_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "template_codes_s")
    private Long id;

    @Column
    private String code;

    @Column
    private String lang;

    @Column(name = "file_type")
    private String fileType;

    @Column
    private String name;

    @Lob
    @Column(name = "binary_file")
    private byte[] binaryFile;
}
