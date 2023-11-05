package org.jasonf.config;

import lombok.extern.slf4j.Slf4j;
import org.jasonf.registry.Registry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

@Slf4j
public class InvokeConfig<T> {
    private Registry registry;

    private Class<T> iface;   // 调用接口

    public InvokeConfig(Class<T> iface) {
        this.iface = iface;
    }

    public Class<T> getIface() {
        return iface;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public T get() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?>[] interfaces = {iface};
        Object proxy = Proxy.newProxyInstance(classLoader, interfaces, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                List<InetSocketAddress> nodes = registry.detect(iface.getName());
                if (log.isDebugEnabled()) {
                    log.debug("node{ip: {}, port: {}}", nodes.get(0).getHostString(), nodes.get(0).getPort());
                }
                // todo 本地缓存服务列表，并添加watcher及时维护
                // todo 支持不同服务调用策略（负载均衡）
                return null;
            }
        });
        return (T) proxy;
    }
}
