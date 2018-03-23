package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.OicImpGateway;
import kz.kegoc.bln.gateway.oic.TelemetryRaw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OicImpGatewayImpl implements OicImpGateway {
    private static final Logger logger = LoggerFactory.getLogger(OicImpGatewayImpl.class);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public OicImpGatewayImpl(OicConfig config, List<Long> points, LocalDateTime atDateTime) {
        this.config = config;
        this.points = points;
        this.atDateTime = atDateTime;
    }

    @Override
    public List<TelemetryRaw> request() throws Exception {
        logger.debug("OicImpGatewayImpl.request started");

        String atDateTimeStr = atDateTime.format(timeFormatter);
        logger.debug("requestedTime: " + atDateTimeStr);

        String pointsStr = points.stream()
            .map(t -> t.toString())
            .collect(Collectors.joining(","));
        logger.debug("points: " + pointsStr);

        logger.debug("Request data from OIC database started");
        List<TelemetryRaw> telemetryList = null;
        try (Connection con = new OicConnectionImpl(config).getConnection()) {
            String sql = "exec master..xp_gettidata2 1, '" + atDateTimeStr + "', " + pointsStr;
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                try (ResultSet rs = pst.executeQuery()) {
                    telemetryList = parseAnswer(rs);
                }
            }
        }
        logger.debug("Request data from OIC database completed");

        logger.debug("OicImpGatewayImpl.request completed");
        return telemetryList;
    }

    private List<TelemetryRaw> parseAnswer(ResultSet rs) throws SQLException {
        List<TelemetryRaw> telemetryList = new ArrayList<>();
        while (rs.next()) {
            Long logti = rs.getLong(1);
            Double val = rs.getDouble(2);
            telemetryList.add(new TelemetryRaw(logti, val));
        }
        return telemetryList;
    }

    private final OicConfig config;
    private final List<Long> points;
    private final LocalDateTime atDateTime;
}
