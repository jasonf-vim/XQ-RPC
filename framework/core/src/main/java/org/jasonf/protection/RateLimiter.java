package org.jasonf.protection;

/**
 * @Author jasonf
 * @Date 2023/11/13
 * @Description
 */

public interface RateLimiter {
    /**
     * 限制请求访问
     *
     * @return true 放行, false 拦截
     */
    boolean allowRequest();
}
