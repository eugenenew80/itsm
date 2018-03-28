package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.OicImpGateway;
import kz.kegoc.bln.gateway.oic.TelemetryRaw;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OicImpGatewayImpl implements OicImpGateway {
    private static final Logger logger = LoggerFactory.getLogger(OicImpGatewayImpl.class);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final OicConfig config;
    private final List<Long> points;
    private final LocalDateTime atDateTime;

    @Override
    public List<TelemetryRaw> request() throws Exception {
        return request(atDateTime);
    }

    @Override
    public List<TelemetryRaw> request(LocalDateTime dateTime) throws Exception {
        validateParams(dateTime);

        logger.debug("OicImpGatewayImpl.request started");
        logger.debug("dateTime: " + dateTime.toString());

        List<TelemetryRaw> telemetryList;
        String sql = "exec master..xp_gettidata2 1, '" + dateTime.format(timeFormatter) + "', " + mapPoints();
        try (Connection con = new OicConnectionImpl(config).getConnection()) {
            logger.debug("Executing SQL command: " + sql);
            try (PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
                telemetryList = parseAnswer(rs);
            }
            logger.debug("Executing SQL command completed, record count: " + telemetryList.size());
        }

        logger.debug("OicImpGatewayImpl.request completed");
        return telemetryList;
    }

    private void validateParams(LocalDateTime dateTime) throws Exception {
        if (dateTime ==null)
            throw new Exception("dateTime  must be specified");
    }

    private String mapPoints() {
        String pointsStr = points.stream()
            .map(t -> t.toString())
            .collect(Collectors.joining(","));

        logger.debug("points: " + pointsStr);
        return pointsStr;
    }

    private List<TelemetryRaw> parseAnswer(ResultSet rs) throws Exception {
        if (!validateAnswer(rs)) return Collections.emptyList();

        List<TelemetryRaw> telemetryList = new ArrayList<>();
        while (rs.next()) {
            Long logti = rs.getLong(1);
            Double val = rs.getDouble(2);
            telemetryList.add(new TelemetryRaw(logti, val));
        }
        return telemetryList;
    }

    private boolean validateAnswer(ResultSet rs) throws SQLException {
        int columnCount = rs.getMetaData().getColumnCount();
        if (columnCount>1)
            return true;

        rs.next();
        logger.warn("Error when parsing answer: " + rs.getString(1));
        return false;
    }
}
