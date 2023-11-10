package org.jasonf.loadbalance;

import java.net.InetSocketAddress;
import java.util.List;

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

    /**
     * 感知服务上下线, 重新负载均衡
     *
     * @param iface     变动的服务接口
     * @param addresses 当前的服务列表
     */
    void reLoadBalance(String iface, List<InetSocketAddress> addresses);
}
