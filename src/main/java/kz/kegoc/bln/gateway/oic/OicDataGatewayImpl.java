package kz.kegoc.bln.gateway.oic;

import kz.kegoc.bln.entity.Telemetry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OicDataGatewayImpl implements OicDataGateway {
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public OicDataGateway config(OicConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public OicDataGateway points(List<Long> points) {
        this.points = points;
        return this;
    }

    @Override
    public List<TelemetryRaw> request(LocalDateTime requestedTime) throws Exception {
        String requestedTimeStr = requestedTime.format(timeFormatter);

        String pointsStr = "";
        for(Object state : points){
            if (pointsStr != null)
                pointsStr=pointsStr + ", " + state.toString();
            else
                pointsStr=pointsStr + state.toString();
        }

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<TelemetryRaw> telemetry = new ArrayList<>();
        try {
            con = new OicConnection(config).getConnection();
            pst = con.prepareStatement("exec master..xp_gettidata2 1, '" + requestedTimeStr + "', " + points);
            rs = pst.executeQuery();

            while (rs.next()) {
                Long logti = rs.getLong(1);
                Double val = rs.getDouble(2);
                TelemetryRaw telemetryRaw = new TelemetryRaw(logti, val);
                telemetry.add(telemetryRaw);
            }
        }

        catch (Exception e) {
            e.printStackTrace();
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

        return telemetry;
    }

    private OicConfig config;
    private List<Long> points;
}
