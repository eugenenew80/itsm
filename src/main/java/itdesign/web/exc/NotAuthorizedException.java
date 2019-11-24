package itdesign.web.exc;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException(String msg) {
        super(msg);
    }
}
