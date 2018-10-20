package bln.imp;

import bln.entity.ArcType;
import bln.gateway.TelemetryRaw;
import bln.web.dto.LogPointCfgDto;

import java.time.LocalDateTime;
import java.util.List;

public interface OicDataReader {
    List<TelemetryRaw> read(ArcType arcType, List<Long> points, LocalDateTime time) throws Exception;

    void addPoints(List<LogPointCfgDto> points);
}
