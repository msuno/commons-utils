package cn.msuno.commons.exception;

public class NgrokClientExecption extends RuntimeException {
    public NgrokClientExecption() {
        super();
    }
    
    public NgrokClientExecption(String message) {
        super(message);
    }
    
    public NgrokClientExecption(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NgrokClientExecption(Throwable cause) {
        super(cause);
    }
    
    protected NgrokClientExecption(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
