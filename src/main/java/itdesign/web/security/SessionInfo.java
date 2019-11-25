package itdesign.web.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SessionInfo {
    private final String sessionKey;
}
