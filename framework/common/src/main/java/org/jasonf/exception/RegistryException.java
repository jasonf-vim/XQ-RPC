package org.jasonf.exception;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public class RegistryException extends RuntimeException {
    public RegistryException() {
    }

    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(Throwable cause) {
        super(cause);
    }

    public RegistryException(String message, Throwable cause) {
        super(message, cause);
    }
}
