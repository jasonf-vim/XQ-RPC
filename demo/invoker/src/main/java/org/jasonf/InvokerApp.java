package org.jasonf;

import org.jasonf.config.InvokeConfig;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

public class InvokerApp {
    public static void main(String[] args) {
        InvokerBootstrap.getInstance()
                .start();

        InvokeConfig<Hello> helloConf = new InvokeConfig<>(Hello.class);
        Hello hello = helloConf.get();    // 获取代理对象
        while (true) {
            System.out.println(hello.greet("XQ"));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
