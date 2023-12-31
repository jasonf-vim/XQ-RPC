package org.jasonf.loadbalance;

import lombok.Setter;
import org.jasonf.registry.Registry;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author jasonf
 * @Date 2023/11/9
 * @Description
 */

@Setter
public abstract class AbstractLoadBalancer implements LoadBalancer {
    private final Map<String, Selector> SERVICE_CACHE = new ConcurrentHashMap<>(128);
    private Registry registry;

    public AbstractLoadBalancer(Registry registry) {
        this.registry = registry;
    }

    @Override
    public InetSocketAddress getServiceAddress(String iface) {
        Selector selector = SERVICE_CACHE.get(iface);
        if (selector == null) {
            selector = getSelector(registry.detect(iface));
            SERVICE_CACHE.put(iface, selector);
        }
        return selector.select();
    }

    @Override
    public void reLoadBalance(String iface, List<InetSocketAddress> addresses) {
        SERVICE_CACHE.put(iface, getSelector(addresses));    // 更新服务列表
    }

    protected abstract Selector getSelector(List<InetSocketAddress> addressList);
}
