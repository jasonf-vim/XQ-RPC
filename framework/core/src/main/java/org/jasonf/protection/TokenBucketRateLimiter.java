package org.jasonf.protection;

/**
 * @Author jasonf
 * @Date 2023/11/13
 * @Description 基于令牌桶的限流器
 */

public class TokenBucketRateLimiter implements RateLimiter {
    private static final int MILLIS = 1000;

    private final int capacity;   // 令牌桶最大容量
    private volatile int token;  // 令牌数量
    private final int rate; // 每秒放置的令牌数量
    private volatile long lastRequest;   // 上一次成功请求的时间

    public TokenBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.token = capacity;  // 初始满令牌
        this.rate = rate;
        this.lastRequest = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean allowRequest() {
        long request = System.currentTimeMillis();
        long interval = request - lastRequest;
        if (interval > MILLIS / rate) token = Math.min(capacity, token + (int) (interval * rate / MILLIS));   // 更新令牌数
        if (token == 0) return false;
        token--;
        lastRequest = request;
        return true;
    }
}
