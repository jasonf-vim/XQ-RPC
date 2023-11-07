package org.jasonf.exception;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public class MessageDecodeException extends RuntimeException {
    public MessageDecodeException() {
    }

    public MessageDecodeException(String message) {
        super(message);
    }

    public MessageDecodeException(Throwable cause) {
        super(cause);
    }

    public MessageDecodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
