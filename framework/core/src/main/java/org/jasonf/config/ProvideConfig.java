package org.jasonf.config;

import lombok.Getter;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

@Getter
public class ProvideConfig<T> {
    private Class<T> iface;   // 服务接口
    private T impl;     // 服务接口实现类

    public ProvideConfig(Class<T> iface, T impl) {
        this.iface = iface;
        this.impl = impl;
    }
}
