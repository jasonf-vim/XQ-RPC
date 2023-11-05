package org.jasonf;

import org.jasonf.boot.Bootstrap;
import org.jasonf.config.InvokeConfig;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

public class InvokerBootstrap extends Bootstrap {
    private InvokerBootstrap() {
    }

    public static Bootstrap getInstance() {
        if (bootstrap == null) {
            synchronized (InvokerBootstrap.class) {
                if (bootstrap == null) {
                    bootstrap = new InvokerBootstrap();
                }
            }
        }
        return bootstrap;
    }

    @Override
    public void retrieval(InvokeConfig<?> config) {
        config.setRegistry(registry);
    }
}
