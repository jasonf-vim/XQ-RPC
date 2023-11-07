package org.jasonf.exception;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public class MessageEncodeException extends RuntimeException {
    public MessageEncodeException() {
    }

    public MessageEncodeException(String message) {
        super(message);
    }

    public MessageEncodeException(Throwable cause) {
        super(cause);
    }

    public MessageEncodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
