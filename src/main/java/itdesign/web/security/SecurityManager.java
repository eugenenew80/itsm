package itdesign.web.security;

public class SecurityManager {
    private static ThreadLocal<SessionInfo> threadLocal = new ThreadLocal<>();

    public void initSession(String sessionKey) {
        threadLocal.set(new SessionInfo(sessionKey));
    }

    public SessionInfo currentSession() {
        return threadLocal.get();
    }
}
