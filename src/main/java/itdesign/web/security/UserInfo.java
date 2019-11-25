package itdesign.web.security;

import lombok.Data;
import java.util.Set;

@Data
public class UserInfo {
    private String userName;
    private String idn;
    private String region;
    private String organ;
    private Set<String> roles;
}
