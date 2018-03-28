package kz.kegoc.bln.gateway.oic;

import javax.sql.RowSet;

public interface OicDatabase {
    RowSet execStatement(String sql) throws Exception;
}
