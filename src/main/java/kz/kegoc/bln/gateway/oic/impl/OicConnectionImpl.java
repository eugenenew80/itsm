package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.OicConnection;
import kz.kegoc.bln.gateway.oic.ServerNum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

@RequiredArgsConstructor
public class OicConnectionImpl implements OicConnection {
    private static final Logger logger = LoggerFactory.getLogger(OicConnectionImpl.class);
    private final OicConfig config;

    public Connection getConnection() throws Exception {
        ServerNum activeServer = findServer();
        logger.info("Active server: " + activeServer);

        logger.debug("Connecting to server started: " + activeServer);
        String conStr = config.urlOic(activeServer);
        Connection connection = DriverManager.getConnection(conStr);
        logger.debug("Connecting to server is successful");

        return connection;
    }

    private ServerNum findServer() {
        if (ping(ServerNum.OIC_01)) return ServerNum.OIC_01;
        if (ping(ServerNum.OIC_02)) return ServerNum.OIC_02;

        throw new RuntimeException("No server available");
    }

    private boolean ping(ServerNum serverNum) {
        logger.debug("Pinging server started: " + serverNum);

        String conStr = config.urlMaster(serverNum);
        String sql = "select status from [dbo].sysdatabases t WHERE t.name='OICDB'";
        try (Connection con = DriverManager.getConnection(conStr);
             PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            if (!rs.next() || rs.getInt(1) != 16)
                throw new RuntimeException("Database is not active");
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
