package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.entity.ArcType;
import kz.kegoc.bln.gateway.oic.OicDatabase;
import kz.kegoc.bln.gateway.oic.OicImpGateway;
import kz.kegoc.bln.gateway.oic.TelemetryRaw;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
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

    private final OicDatabase oicDatabase;
    private final List<Long> points;
    private final ArcType arcType;

    @Override
    public List<TelemetryRaw> request(LocalDateTime dateTime) throws Exception {
        validateParams(dateTime);

        logger.debug("request started");
        logger.debug("dateTime: " + dateTime.toString());

        String sql = "exec master..xp_gettidata2 " + arcType.getOicArcId().toString() + ", '" + dateTime.format(timeFormatter) + "', " + mapPoints();
        RowSet rs = oicDatabase.execStatement(sql);
        List<TelemetryRaw> telemetries = parseAnswer(rs);

        logger.debug("request completed");
        return telemetries;
    }

    private void validateParams(LocalDateTime dateTime) throws Exception {
        if (dateTime == null)
            throw new Exception("dateTime  must be specified");
    }

    private String mapPoints() {
        String pointsStr = points.stream()
            .map(t -> t.toString())
            .collect(Collectors.joining(","));

        logger.debug("points: " + pointsStr);
        return pointsStr;
    }

    private List<TelemetryRaw> parseAnswer(RowSet rs) throws Exception {
        if (!validateAnswer(rs)) return Collections.emptyList();

        List<TelemetryRaw> telemetries = new ArrayList<>();
        while (rs.next()) {
            Long logti = rs.getLong(1);
            Double val = rs.getDouble(2);
            telemetries.add(new TelemetryRaw(logti, val));
        }
        return telemetries;
    }

    private boolean validateAnswer(RowSet rs) throws SQLException {
        int columnCount = rs.getMetaData().getColumnCount();
        if (columnCount>1)
            return true;

        rs.next();
        logger.warn("Error when parsing answer: " + rs.getString(1));
        return false;
    }
}
