package org.jasonf.exception;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException() {
    }

    public ServiceNotFoundException(String message) {
        super(message);
    }

    public ServiceNotFoundException(Throwable cause) {
        super(cause);
    }

    public ServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
