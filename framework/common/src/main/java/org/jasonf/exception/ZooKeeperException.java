package org.jasonf.exception;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public class ZooKeeperException extends RuntimeException {
    public ZooKeeperException() {
    }

    public ZooKeeperException(String message) {
        super(message);
    }

    public ZooKeeperException(Throwable cause) {
        super(cause);
    }

    public ZooKeeperException(String message, Throwable cause) {
        super(message, cause);
    }
}
