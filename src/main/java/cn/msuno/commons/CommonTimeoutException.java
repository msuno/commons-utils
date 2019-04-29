package cn.msuno.commons;

public class CommonTimeoutException extends Exception {

    private static final long serialVersionUID = 1L;

    public CommonTimeoutException() {
        super();
    }

    public CommonTimeoutException(String message) {
        super(message);
    }

    public CommonTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonTimeoutException(Throwable cause) {
        super(cause);
    }
}
