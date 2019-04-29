package cn.msuno.commons;

public class CommonRuntimeException extends Exception {

    private static final long serialVersionUID = 1L;

    public CommonRuntimeException() {
        super();
    }

    public CommonRuntimeException(String message) {
        super(message);
    }

    public CommonRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonRuntimeException(Throwable cause) {
        super(cause);
    }
}
