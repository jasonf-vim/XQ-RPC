package org.jasonf.config;

import lombok.extern.slf4j.Slf4j;
import org.jasonf.proxy.RPCInvocationHandler;

import java.lang.reflect.Proxy;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

@Slf4j
public class InvokeConfig<T> {
    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private static Class<?>[] interfaces;
    private static final RPCInvocationHandler INVOCATION_HANDLER = new RPCInvocationHandler();

    public InvokeConfig(Class<T> iface) {
        interfaces = new Class[]{iface};
        INVOCATION_HANDLER.setIface(iface.getName());
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) Proxy.newProxyInstance(CLASS_LOADER, interfaces, INVOCATION_HANDLER);
    }
}
