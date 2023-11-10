package org.jasonf.loadbalance.impl;

import org.jasonf.loadbalance.AbstractLoadBalancer;
import org.jasonf.loadbalance.Selector;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author jasonf
 * @Date 2023/11/9
 * @Description 轮询
 */

public class RoundRobinLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected Selector getSelector(List<InetSocketAddress> addressList) {
        return new RoundRobinSelector(addressList);
    }

    private static class RoundRobinSelector implements Selector {
        List<InetSocketAddress> addressList;
        // 考虑多个线程同时调用同一服务接口
        AtomicInteger cursor = new AtomicInteger();

        public RoundRobinSelector(List<InetSocketAddress> addressList) {
            this.addressList = addressList;
        }

        @Override
        public InetSocketAddress select() {
            if (addressList.isEmpty()) throw new RuntimeException("服务列表为空");
            if (cursor.get() == addressList.size() - 1) cursor.set(0);
            else cursor.incrementAndGet();
            return addressList.get(cursor.get());
        }
    }
}
