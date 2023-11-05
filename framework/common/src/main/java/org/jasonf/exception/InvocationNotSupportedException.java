package org.jasonf.exception;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public class InvocationNotSupportedException extends RuntimeException {
    public InvocationNotSupportedException() {
    }

    public InvocationNotSupportedException(String message) {
        super(message);
    }

    public InvocationNotSupportedException(Throwable cause) {
        super(cause);
    }

    public InvocationNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
