package kz.kegoc.bln.gateway.oic;

import lombok.Data;

@Data
public class OicProperties {
    private String serverOne;
    private String serverTwo;
    private int port;
    private String user;
    private String pass;
    private String masterDb;
    private String oicDb;
}
