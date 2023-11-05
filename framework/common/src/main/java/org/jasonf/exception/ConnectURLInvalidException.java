package org.jasonf.exception;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public class ConnectURLInvalidException extends RuntimeException {
    public ConnectURLInvalidException() {
    }

    public ConnectURLInvalidException(String message) {
        super(message);
    }

    public ConnectURLInvalidException(Throwable cause) {
        super(cause);
    }

    public ConnectURLInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
