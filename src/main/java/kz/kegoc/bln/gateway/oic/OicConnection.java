package kz.kegoc.bln.gateway.oic;

import java.sql.Connection;

public interface OicConnection {
    Connection getConnection() throws Exception;
}
