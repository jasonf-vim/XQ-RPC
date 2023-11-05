package org.jasonf.config;

import org.jasonf.Constant;
import org.jasonf.exception.ConnectURLInvalidException;
import org.jasonf.exception.RegistryException;
import org.jasonf.registry.Registry;
import org.jasonf.registry.impl.SampleRegistry;
import org.jasonf.registry.impl.ZooKeeperRegistry;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public class RegistryConfig {
    private String component;
    private String ipAddr;
    private String port;

    public RegistryConfig(String connectURL) {
        String[] attrs, location;
        if (connectURL == null ||
                (attrs = connectURL.split("://")).length < 2 ||
                (location = attrs[1].split(":")).length < 2)
            throw new ConnectURLInvalidException("连接注册中心的URL不合法");
        component = attrs[0];
        ipAddr = location[0];
        port = location[1];
    }

    /**
     * 简单工厂
     *
     * @return 与 connectURL 匹配的注册中心
     */
    public Registry getRegistry() {
        if ("ZooKeeper".equalsIgnoreCase(component))
            return new ZooKeeperRegistry(ipAddr + ":" + port, Constant.DEFAULT_TIMEOUT);
        if ("Sample".equalsIgnoreCase(component))
            return new SampleRegistry();
        throw new RegistryException("注册中心未找到或者不受支持");
    }
}
