package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.OicImpGateway;
import kz.kegoc.bln.gateway.oic.TelemetryRaw;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        logger.debug("OicImpGatewayImpl.request started");
        logger.debug("atDateTime: " + atDateTime.toString());

        String pointsStr = points.stream()
            .map(t -> t.toString())
            .collect(Collectors.joining(","));
        logger.debug("points: " + pointsStr);

        List<TelemetryRaw> telemetryList;
        try (Connection con = new OicConnectionImpl(config).getConnection()) {
            String sql = "exec master..xp_gettidata2 1, '" + atDateTime.format(timeFormatter) + "', " + pointsStr;
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                try (ResultSet rs = pst.executeQuery()) {
                    telemetryList = parseAnswer(rs);
                }
            }
        }

        logger.debug("OicImpGatewayImpl.request completed");
        return telemetryList;
    }

    private List<TelemetryRaw> parseAnswer(ResultSet rs) throws Exception {
        List<TelemetryRaw> telemetryList = new ArrayList<>();
        while (rs.next()) {
            Long logti = rs.getLong(1);
            Double val = rs.getDouble(2);
            telemetryList.add(new TelemetryRaw(logti, val));
        }
        return telemetryList;
    }
}
