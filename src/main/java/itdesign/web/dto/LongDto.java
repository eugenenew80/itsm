package itdesign.web.dto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiModel(value = "Long", description = "Целое число")
@Getter
@RequiredArgsConstructor
public class LongDto {
    private final Long value;
}
