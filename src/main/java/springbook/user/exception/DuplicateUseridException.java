package springbook.user.exception;

public class DuplicateUseridException extends RuntimeException {
    public DuplicateUseridException(Throwable cause) {
        super(cause);
    }
}
