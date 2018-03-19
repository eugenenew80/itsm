package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.OicConnection;
import kz.kegoc.bln.gateway.oic.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class OicConnectionImpl implements OicConnection {
    private static final Logger logger = LoggerFactory.getLogger(OicConnectionImpl.class);

    public OicConnectionImpl(OicConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws Exception {
        boolean active01 = ping(config.buildUrlMaster(Server.ServerOne));
        boolean active02 = ping(config.buildUrlMaster(Server.ServerTwo));

        if (!active01 && !active02)
            throw new RuntimeException("No server available");

        String conStr = active01 ? config.buildUrlOIC(Server.ServerOne) : config.buildUrlOIC(Server.ServerTwo);
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
            return false;
        }

        return true;
    }

    private final OicConfig config;
}
