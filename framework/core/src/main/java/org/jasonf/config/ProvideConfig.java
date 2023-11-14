package org.jasonf.config;

import lombok.Getter;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

@Getter
public class ProvideConfig<T> {
    private Class<?> iface;   // 服务接口
    private String group;
    private T impl;     // 服务接口实现类

    public ProvideConfig(Class<?> iface, String group, T impl) {
        this.iface = iface;
        this.group = group;
        this.impl = impl;
    }
}
