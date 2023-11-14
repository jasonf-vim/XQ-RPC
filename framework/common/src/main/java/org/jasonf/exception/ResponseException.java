package org.jasonf.exception;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public class ResponseException extends RuntimeException {
    public ResponseException() {
    }

    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(Throwable cause) {
        super(cause);
    }

    public ResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
