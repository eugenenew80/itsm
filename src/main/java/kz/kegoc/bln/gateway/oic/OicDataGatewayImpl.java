package kz.kegoc.bln.gateway.oic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        String pointsStr = points.stream()
            .map(t -> t.toString())
            .collect(Collectors.joining(","));

        List<TelemetryRaw> telemetry = new ArrayList<>();
        try (Connection con = new OicConnection(config).getConnection()) {
            try (PreparedStatement pst = con.prepareStatement("exec master..xp_gettidata2 1, '" + requestedTimeStr + "', " + pointsStr)) {
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        Long logti = rs.getLong(1);
                        Double val = rs.getDouble(2);
                        telemetry.add(new TelemetryRaw(logti, val));
                    }
                }
            }
        }

        return telemetry;
    }

    private OicConfig config;
    private List<Long> points;
}
