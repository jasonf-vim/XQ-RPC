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
public class InvokeConfig {
    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private static final RPCInvocationHandler INVOCATION_HANDLER = new RPCInvocationHandler();

    public static Object get(Class<?> iface) {
        INVOCATION_HANDLER.setIface(iface.getName());
        return Proxy.newProxyInstance(CLASS_LOADER, new Class[]{iface}, INVOCATION_HANDLER);
    }
}
