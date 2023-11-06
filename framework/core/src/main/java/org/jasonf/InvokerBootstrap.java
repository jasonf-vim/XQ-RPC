package org.jasonf;

import io.netty.channel.Channel;
import org.jasonf.boot.Bootstrap;
import org.jasonf.config.InvokeConfig;

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
    public void retrieval(InvokeConfig<?> config) {
        config.setRegistry(registry);
    }
}
