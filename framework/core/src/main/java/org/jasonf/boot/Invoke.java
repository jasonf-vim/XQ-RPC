package org.jasonf.boot;

import org.jasonf.exception.InvocationNotSupportedException;
import org.jasonf.loadbalance.AbstractLoadBalancer;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public interface Invoke {
    default void loadBalancer(AbstractLoadBalancer loadBalancer) {
        throw new InvocationNotSupportedException("当前方法调用不受支持");
    }
}
