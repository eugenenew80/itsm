package kz.kegoc.bln.gateway.oic;

import kz.kegoc.bln.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class OicConnection {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public OicConnection(OicConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws Exception {
        boolean active01 = ping(config.buildUrlMaster(ServerType.ServerOne));
        boolean active02 = ping(config.buildUrlMaster(ServerType.ServerTwo));

        if (!active01 && !active02)
            throw new RuntimeException("No server available");

        String conStr = active01 ? config.buildUrlOIC(ServerType.ServerOne) : config.buildUrlOIC(ServerType.ServerTwo);
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
