package org.jasonf.boot;

import org.jasonf.config.RegistryConfig;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public abstract class Bootstrap implements Provide, Invoke {
    protected static volatile Bootstrap bootstrap;

    protected Configuration config = new Configuration();

    protected Bootstrap() {
    }

    public Configuration getConfig() {
        return config;
    }

    public Bootstrap application(String appName) {
        config.setAppName(appName);
        return this;
    }

    public Bootstrap registry(RegistryConfig config) {
        this.config.setRegistry(config.getRegistry());
        return this;
    }

    public abstract void start();
}
