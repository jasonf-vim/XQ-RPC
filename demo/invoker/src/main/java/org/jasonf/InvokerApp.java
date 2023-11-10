package org.jasonf;

import org.jasonf.config.InvokeConfig;
import org.jasonf.config.RegistryConfig;
import org.jasonf.heartbeat.HeartbeatDetector;
import org.jasonf.loadbalance.AbstractLoadBalancer;
import org.jasonf.loadbalance.impl.ConsistentHashLoadBalancer;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

public class InvokerApp {
    public static void main(String[] args) {
        RegistryConfig registryConfig = new RegistryConfig("zookeeper://123.60.86.242:2181");
        AbstractLoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

        InvokerBootstrap.getInstance()
                .application("XQ-invoker")
                .registry(registryConfig)
                .loadBalancer(loadBalancer);

        HeartbeatDetector.start();    // 开启心跳检测

        InvokeConfig<Hello> helloConf = new InvokeConfig<>(Hello.class);
        Hello hello = helloConf.get();  // 获取代理对象
        for (int i = 0; i < 10; i++) {
            System.out.println(hello.greet("XQ"));
        }
    }
}
