package org.jasonf.loadbalance;

import java.net.InetSocketAddress;

/**
 * @Author jasonf
 * @Date 2023/11/9
 * @Description 负载均衡器
 */

public interface LoadBalancer {
    /**
     * 负载均衡
     *
     * @param iface 服务接口全限定名
     * @return 提供服务的节点地址
     */
    InetSocketAddress getServiceAddress(String iface);
}
