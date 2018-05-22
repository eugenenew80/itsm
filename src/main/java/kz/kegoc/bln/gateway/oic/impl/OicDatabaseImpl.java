package kz.kegoc.bln.gateway.oic.impl;

import com.sun.rowset.CachedRowSetImpl;
import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.OicDatabase;
import kz.kegoc.bln.gateway.oic.ServerNumEnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.RowSet;
import java.sql.*;

@RequiredArgsConstructor
public class OicDatabaseImpl implements OicDatabase {
    private static final Logger logger = LoggerFactory.getLogger(OicDatabaseImpl.class);
    private final OicConfig config;

    public RowSet execStatement(String sql) throws Exception {
        logger.debug("Executing SQL command: " + sql);
        CachedRowSetImpl crs = new CachedRowSetImpl();
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            crs.populate(rs);
        }
        logger.debug("Executing SQL command completed, record count: " + crs.size());
        return crs;
    }

    private Connection getConnection() throws Exception {
        ServerNumEnum activeServer = findServer();
        logger.info("Active server: " + activeServer);

        logger.debug("Connecting to server started: " + activeServer);
        Connection connection = DriverManager.getConnection(config.urlOic(activeServer));
        logger.debug("Connecting to server is successful");

        return connection;
    }

    private ServerNumEnum findServer() {
        if (checkServer(ServerNumEnum.OIC_01)) return ServerNumEnum.OIC_01;
        if (checkServer(ServerNumEnum.OIC_02)) return ServerNumEnum.OIC_02;
        throw new RuntimeException("No server available");
    }

    private boolean checkServer(ServerNumEnum serverNum) {
        logger.debug("Checking server started: " + serverNum);

        String conStr = config.urlMaster(serverNum);
        String sql = "select status from [dbo].sysdatabases t WHERE t.name='OICDB'";
        try (Connection con = DriverManager.getConnection(conStr); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            logger.debug("Checking sysdatabases table");
            if (rs.next()) {
                int status = rs.getInt(1);
                logger.debug("status: " + status);
                if (status >99)
                    throw new RuntimeException("Database is available, but is not active");
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(conStr);
            return false;
        }
        logger.debug("Pinging server is successful");
        return true;
    }
}
