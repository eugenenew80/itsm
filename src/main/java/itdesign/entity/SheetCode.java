package itdesign.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@EqualsAndHashCode(of= {"id"})
@NoArgsConstructor
@Entity
@Table(name = "sheet_codes")
public class SheetCode {

    @Id
    @SequenceGenerator(name="sheet_codes_s", sequenceName = "sheet_codes_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sheet_codes_s")
    private Long id;

    @Column
    private String code;

    @Column
    private String lang;

    @Column
    private String name;

    @Column
    private String reportCode;
}
