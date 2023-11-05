package org.jasonf;

import org.jasonf.config.ProvideConfig;
import org.jasonf.config.RegistryConfig;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

public class ProviderApp {
    public static void main(String[] args) {
        RegistryConfig registryConfig = new RegistryConfig("zookeeper://123.60.86.242:2181");
        ProvideConfig<Hello> helloConf = new ProvideConfig<>(Hello.class, new HelloWorld());

        ProviderBootstrap.getInstance()
                .application("XQ-provider")
                .registry(registryConfig)
                .release(helloConf)
                .start();
    }
}
