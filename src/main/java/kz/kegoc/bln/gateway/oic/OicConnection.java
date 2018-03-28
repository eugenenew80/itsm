package kz.kegoc.bln.gateway.oic;

import javax.sql.RowSet;

public interface OicConnection {
    RowSet execStatement(String sql) throws Exception;
}
