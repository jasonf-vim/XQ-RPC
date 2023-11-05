package org.jasonf.boot;

import org.jasonf.config.RegistryConfig;
import org.jasonf.registry.Registry;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public class Bootstrap implements Provide, Invoke {
    protected static volatile Bootstrap bootstrap;

    protected String appName = "XQ";
    protected Registry registry;

    protected Bootstrap() {
    }

    public Bootstrap application(String appName) {
        this.appName = appName;
        return this;
    }

    public Bootstrap registry(RegistryConfig config) {
        registry = config.getRegistry();
        return this;
    }
}
