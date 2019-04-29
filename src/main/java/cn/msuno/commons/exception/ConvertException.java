package cn.msuno.commons.exception;

import cn.msuno.commons.exception.CommonRuntimeException;

public class ConvertException extends CommonRuntimeException {
    public ConvertException() {
        super();
    }

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertException(Throwable cause) {
        super(cause);
    }
}
