package kz.kegoc.bln.gateway;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of= {"logti"})
public class TelemetryRaw {
    public TelemetryRaw(Long logti, Double val) {
        this.logti = logti;
        this.val = val;
    }

    private final Long logti;
    private final Double val;
}
