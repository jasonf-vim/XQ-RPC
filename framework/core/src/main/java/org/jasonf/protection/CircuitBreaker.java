package org.jasonf.protection;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author jasonf
 * @Date 2023/11/13
 * @Description 熔断器
 */

public class CircuitBreaker {
    private volatile boolean isOpen;
    private volatile boolean resetting;
    private final AtomicInteger REQ_CNT;
    private final AtomicInteger ERR_CNT;
    private final int MAX_ERROR_COUNT;
    private final int MIN_ERROR_COUNT;
    private final float MAX_ERROR_RATE;

    public CircuitBreaker(int maxErrorCount, int minErrorCount, float maxErrorRate) {
        this.isOpen = false;    // 默认关闭状态
        this.resetting = false;
        this.REQ_CNT = new AtomicInteger();
        this.ERR_CNT = new AtomicInteger();
        this.MAX_ERROR_COUNT = maxErrorCount;
        this.MIN_ERROR_COUNT = minErrorCount;
        this.MAX_ERROR_RATE = maxErrorRate;
    }

    public void requestCount() {
        REQ_CNT.incrementAndGet();
    }

    public void errorCount() {
        ERR_CNT.incrementAndGet();
    }

    public boolean isResetting() {
        return resetting;
    }

    public void setResetting(boolean status) {
        this.resetting = status;
    }

    public boolean isBreak() {
        if (isOpen) return true;
        if (ERR_CNT.get() > MAX_ERROR_COUNT ||
                (ERR_CNT.get() > MIN_ERROR_COUNT && ((float) ERR_CNT.get() / REQ_CNT.get()) > MAX_ERROR_RATE)) {
            isOpen = true;
            return true;
        }
        return false;
    }

    public void reset() {
        isOpen = false;
        resetting = false;
        REQ_CNT.set(0);
        ERR_CNT.set(0);
    }
}
