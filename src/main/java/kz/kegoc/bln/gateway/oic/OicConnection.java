package kz.kegoc.bln.gateway.oic;

import java.sql.*;

public class OicConnection {
    public OicConnection(OicConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws Exception {
        boolean active01 = false;
        boolean active02 = false;

        active01 = ping(config.buildUrlMaster(ServerType.SERVER1));
        if (!active01)
            active02 = ping(config.buildUrlMaster(ServerType.SERVER2));

        if (!active01 && !active02)
            throw new RuntimeException("No server available");

        String conStr = active01 ? config.buildUrlOIC(ServerType.SERVER1) : config.buildUrlOIC(ServerType.SERVER2);
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(conStr);
    }


    private boolean ping(String conStr) {
        Connection con = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con= DriverManager.getConnection(conStr);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement("select status from [dbo].sysdatabases t WHERE t.name='OICDB'");
            rs = pst.executeQuery();

            if (!rs.next())
                return false;

            if (rs.getInt(1)>99)
                return false;
        }
        catch (Exception e) {
            return false;
        }

        finally {
            try {
                if (rs!=null) rs.close();
            }
            catch (Exception exc) {}

            try {
                if (con != null) con.close();
            }
            catch (Exception exc) {}
        }

        return true;
    }

    private final OicConfig config;
}
