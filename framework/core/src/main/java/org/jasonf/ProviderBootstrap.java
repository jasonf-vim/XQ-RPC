package org.jasonf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.boot.Bootstrap;
import org.jasonf.channel.handler.MethodInvokeHandler;
import org.jasonf.channel.handler.RequestDecoder;
import org.jasonf.config.ProvideConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

@Slf4j
public class ProviderBootstrap extends Bootstrap {
    public static final Map<String, ProvideConfig<?>> SERVICES = new ConcurrentHashMap<>();

    private int port = 8102;

    private ProviderBootstrap() {
    }

    public static Bootstrap getInstance() {
        if (bootstrap == null) {
            synchronized (ProviderBootstrap.class) {
                if (bootstrap == null) {
                    bootstrap = new ProviderBootstrap();
                }
            }
        }
        return bootstrap;
    }

    @Override
    public Bootstrap release(ProvideConfig<?> config) {
        String iface = config.getIface().getName();
        registry.register(iface);   // 对外暴露服务
        SERVICES.put(iface, config);    // 维护本地 iface -> impl 映射关系
        return this;
    }

    @Override
    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup(2);
        EventLoopGroup worker = new NioEventLoopGroup(10);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new LoggingHandler())
                                    .addLast(new RequestDecoder())
                                    .addLast(new MethodInvokeHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
