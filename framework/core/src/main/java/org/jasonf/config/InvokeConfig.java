package org.jasonf.config;

import lombok.extern.slf4j.Slf4j;
import org.jasonf.proxy.RPCInvocationHandler;
import org.jasonf.registry.Registry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

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

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?>[] interfaces = {iface};
        InvocationHandler invocationHandler = new RPCInvocationHandler(registry, iface.getName());
        return (T) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }
}
