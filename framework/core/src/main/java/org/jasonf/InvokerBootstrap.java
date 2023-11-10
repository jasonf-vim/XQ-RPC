package org.jasonf;

import io.netty.channel.Channel;
import org.jasonf.boot.Bootstrap;
import org.jasonf.loadbalance.AbstractLoadBalancer;
import org.jasonf.watcher.ServiceMutation;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

public class InvokerBootstrap extends Bootstrap {
    public static final Map<InetSocketAddress, Channel> CHANNEL_CACHE = new ConcurrentHashMap<>();

    public static final Map<Long, CompletableFuture<Object>> PENDING_REQUEST = new ConcurrentHashMap<>(64);

    public static final IDGenerator ID_GENERATOR = new IDGenerator(Constant.DATA_CENTER_ID, Constant.MACHINE_ID);

    public static final ThreadLocal<Long> MESSAGE_ID_THREAD_LOCAL = new ThreadLocal<>();

    public static AbstractLoadBalancer loadBalancer;

    public static final Map<InetSocketAddress, Long> RESPONSE_TIME = new ConcurrentHashMap<>(128);

    public static final ServiceMutation SERVICE_MUTATION = new ServiceMutation();

    private InvokerBootstrap() {
    }

    public static Bootstrap getInstance() {
        if (bootstrap == null) {
            synchronized (InvokerBootstrap.class) {
                if (bootstrap == null) {
                    bootstrap = new InvokerBootstrap();
                }
            }
        }
        return bootstrap;
    }

    @Override
    public Bootstrap activateWatcher() {
        SERVICE_MUTATION.setRegistry(registry);
        return this;
    }

    @Override
    public void loadBalancer(AbstractLoadBalancer loadBalancer) {
        loadBalancer.setRegistry(registry);
        InvokerBootstrap.loadBalancer = loadBalancer;
    }
}
