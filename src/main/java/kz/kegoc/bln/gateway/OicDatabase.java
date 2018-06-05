package kz.kegoc.bln.gateway;

import javax.sql.RowSet;

public interface OicDatabase {
    RowSet execStatement(String sql) throws Exception;
}
