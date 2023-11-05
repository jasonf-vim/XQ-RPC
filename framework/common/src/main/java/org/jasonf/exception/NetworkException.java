package org.jasonf.exception;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public class NetworkException extends RuntimeException {
    public NetworkException() {
    }

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
