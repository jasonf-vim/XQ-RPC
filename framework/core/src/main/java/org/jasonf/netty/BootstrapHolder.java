package org.jasonf.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.channel.InvokerChannelInitializer;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

@Slf4j
public class BootstrapHolder {
    private static final Bootstrap BOOTSTRAP;

    static {
        BOOTSTRAP = new Bootstrap();
        BOOTSTRAP.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new InvokerChannelInitializer());
    }

    public static Bootstrap getBootstrap() {
        return BOOTSTRAP;
    }
}
