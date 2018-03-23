package kz.kegoc.bln.gateway.oic.impl;

import kz.kegoc.bln.gateway.oic.OicConfig;
import kz.kegoc.bln.gateway.oic.OicImpGateway;
import kz.kegoc.bln.gateway.oic.TelemetryRaw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("defOicImpGateway")
@Primary
public class OicImpGatewayImpl implements OicImpGateway {
    private static final Logger logger = LoggerFactory.getLogger(OicImpGatewayImpl.class);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public OicImpGateway config(OicConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public OicImpGateway points(List<Long> points) {
        this.points = points;
        return this;
    }

    @Override
    public List<TelemetryRaw> request(LocalDateTime requestedTime) throws Exception {
        logger.debug("OicImpGatewayImpl.request started");

        if (requestedTime==null)
            throw new Exception("requestedTime  must be specified");

        if (points==null || points.isEmpty())
            throw new Exception("points must be specified");

        if (config==null)
            throw new Exception("config  must be specified");

        String requestedTimeStr = requestedTime.format(timeFormatter);
        logger.debug("requestedTime: " + requestedTimeStr);

        String pointsStr = points.stream()
            .map(t -> t.toString())
            .collect(Collectors.joining(","));
        logger.debug("points: " + pointsStr);

        logger.debug("Request data from OIC database started");
        List<TelemetryRaw> telemetryList = new ArrayList<>();
        try (Connection con = new OicConnectionImpl(config).getConnection()) {
            String sql = "exec master..xp_gettidata2 1, '" + requestedTimeStr + "', " + pointsStr;
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        Long logti = rs.getLong(1);
                        Double val = rs.getDouble(2);
                        telemetryList.add(new TelemetryRaw(logti, val));
                    }
                }
            }
        }
        logger.debug("Request data from OIC database completed");

        logger.debug("OicImpGatewayImpl.request completed");
        return telemetryList;
    }

    private OicConfig config;
    private List<Long> points;
}
