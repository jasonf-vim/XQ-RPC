package org.jasonf.loadbalance;

import java.net.InetSocketAddress;

/**
 * @Author jasonf
 * @Date 2023/11/9
 * @Description 负载均衡策略
 */

public interface Selector {
    /**
     * 负载均衡
     *
     * @return 提供服务的节点地址
     */
    InetSocketAddress select();
}
