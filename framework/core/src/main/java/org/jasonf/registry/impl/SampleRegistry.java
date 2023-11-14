package org.jasonf.registry.impl;

import org.jasonf.registry.Registry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description 其他注册中心, 例如 redis、nacos 等
 */

public class SampleRegistry implements Registry {
    @Override
    public void register(String iface, String group) {

    }

    @Override
    public List<InetSocketAddress> detect(String iface) {
        return null;
    }

    @Override
    public void disconnect() {

    }
}
