package kz.kegoc.bln.imp;

import kz.kegoc.bln.entity.ArcType;
import kz.kegoc.bln.gateway.TelemetryRaw;
import kz.kegoc.bln.web.dto.LogPointCfgDto;

import java.time.LocalDateTime;
import java.util.List;

public interface OicDataReader {
    List<TelemetryRaw> read(ArcType arcType, List<Long> points, LocalDateTime time) throws Exception;

    void addPoints(List<LogPointCfgDto> points);
}
