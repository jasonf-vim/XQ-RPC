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

        InvokeConfig config = new InvokeConfig();
        config.setInterface(Hello.class);
        Hello hello = (Hello) config.get();    // 获取代理对象
        for (int i = 0; i < 500; i++) {
            try {
                System.out.println(hello.greet("XQ"));
            } catch (RuntimeException ex) {
                System.out.println("method invoke fail.");
            }
        }
    }
}
