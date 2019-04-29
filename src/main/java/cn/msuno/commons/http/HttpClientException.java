package cn.msuno.commons.http;

import cn.msuno.commons.CommonRuntimeException;

public class HttpClientException extends CommonRuntimeException {
    public HttpClientException() {
        super();
    }

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientException(Throwable cause) {
        super(cause);
    }
}
