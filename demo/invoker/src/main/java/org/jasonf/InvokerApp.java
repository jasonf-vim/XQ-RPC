package org.jasonf;

import org.jasonf.config.InvokeConfig;
import org.jasonf.config.RegistryConfig;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

public class InvokerApp {
    public static void main(String[] args) {
        RegistryConfig registryConfig = new RegistryConfig("zookeeper://123.60.86.242:2181");
        InvokeConfig<Hello> helloConf = new InvokeConfig<>(Hello.class);

        InvokerBootstrap.getInstance()
                .application("XQ-invoker")
                .registry(registryConfig)
                .retrieval(helloConf);

        // 获取代理对象:
        // 1、连接注册中心
        // 2、拉取服务列表
        // 3、选择服务节点并建立连接
        // 4、发送请求（节点坐标、接口名、形参列表、实参列表）
        // 5、接收结果并返回
        Hello hello = helloConf.get();
        hello.greet("XQ");
    }
}
