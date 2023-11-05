package org.jasonf;

import org.jasonf.boot.Bootstrap;
import org.jasonf.config.ProvideConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

public class ProviderBootstrap extends Bootstrap {
    private final Map<String, ProvideConfig<?>> SERVICES = new HashMap<>();

    private ProviderBootstrap() {
    }

    public static Bootstrap getInstance() {
        if (bootstrap == null) {
            synchronized (ProviderBootstrap.class) {
                if (bootstrap == null) {
                    bootstrap = new ProviderBootstrap();
                }
            }
        }
        return bootstrap;
    }

    @Override
    public Bootstrap release(ProvideConfig<?> config) {
        String iface = config.getIface().getName();
        registry.register(iface);   // 对外暴露服务
        SERVICES.put(iface, config);    // 维护本地 iface -> impl 映射关系
        return this;
    }

    @Override
    public void start() {
        try {
            Thread.sleep(60000);   // 60 sec
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
