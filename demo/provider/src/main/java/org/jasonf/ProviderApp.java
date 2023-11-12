package org.jasonf;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

public class ProviderApp {
    public static void main(String[] args) {
        ProviderBootstrap.getInstance()
                .scan("org.jasonf.impl")
                .start();
    }
}
