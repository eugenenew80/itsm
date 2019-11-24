package itdesign.web.exc;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String msg) {
        super(msg);
    }
}
