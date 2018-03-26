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
        boolean active01 = ping(config.buildUrlMaster(ServerNum.OIC01));
        boolean active02 = ping(config.buildUrlMaster(ServerNum.OIC02));

        if (!active01 && !active02)
            throw new RuntimeException("No server available");

        String conStr = active01 ? config.buildUrlOIC(ServerNum.OIC01) : config.buildUrlOIC(ServerNum.OIC02);
        return DriverManager.getConnection(conStr);
    }

    private boolean ping(String conStr) {
        try (Connection con = DriverManager.getConnection(conStr)) {
            try (PreparedStatement pst = con.prepareStatement("select status from [dbo].sysdatabases t WHERE t.name='OICDB'")) {
                try (ResultSet rs = pst.executeQuery()) {
                    if (!rs.next())
                        throw new RuntimeException("No data in sysdatabases table");

                    if (rs.getInt(1) > 99)
                        throw new RuntimeException("Database is not active");
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(conStr);
            return false;
        }

        return true;
    }
}
