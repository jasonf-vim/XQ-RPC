package org.jasonf;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author jasonf
 * @Date 2023/11/15
 * @Description
 */

@Component
public class RpcStarter implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        InvokerBootstrap.getInstance()
                .start();
    }
}
