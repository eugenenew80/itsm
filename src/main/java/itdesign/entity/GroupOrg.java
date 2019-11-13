package itdesign.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode(of= {"groupCode, orgCode"})
@NoArgsConstructor
@Entity
@Table(name = "group_orgs")
@IdClass(GroupOrg.class)
public class GroupOrg implements Serializable {

    @Id
    @Column(name = "group_code")
    private String groupCode;

    @Id
    @Column(name = "org_code")
    private String orgCode;
}