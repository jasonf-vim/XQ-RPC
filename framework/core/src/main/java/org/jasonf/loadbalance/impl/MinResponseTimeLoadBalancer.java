package org.jasonf.loadbalance.impl;

import org.jasonf.InvokerBootstrap;
import org.jasonf.loadbalance.AbstractLoadBalancer;
import org.jasonf.loadbalance.Selector;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author jasonf
 * @Date 2023/11/10
 * @Description 最短响应时间
 */

public class MinResponseTimeLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected Selector getSelector(List<InetSocketAddress> addressList) {
        return new MinResponseTimeSelector(addressList);
    }

    private static class MinResponseTimeSelector implements Selector {
        List<InetSocketAddress> addressList;

        public MinResponseTimeSelector(List<InetSocketAddress> addressList) {
            this.addressList = addressList;
        }

        @Override
        public InetSocketAddress select() {
            // 遍历列表, 计算最短响应时间, 返回对应的 address
            InetSocketAddress addr = null;
            long min = Long.MAX_VALUE;
            for (InetSocketAddress address : addressList) {
                Long time = InvokerBootstrap.RESPONSE_TIME.get(address);
                if (time == null) return address;   // 纳入新的节点
                if (time < min) {
                    min = time;
                    addr = address;
                }
            }
            return addr;
        }
    }
}
