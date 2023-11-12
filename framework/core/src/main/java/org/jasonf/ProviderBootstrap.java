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
import org.jasonf.annotation.XQ;
import org.jasonf.boot.Bootstrap;
import org.jasonf.channel.handler.*;
import org.jasonf.config.ProvideConfig;
import org.jasonf.util.PackageUtil;

import java.util.List;
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
    public void release(ProvideConfig<?> config) {
        String iface = config.getIface().getName();
        super.config.getRegistry().register(iface);   // 对外暴露服务
        SERVICES.put(iface, config);    // 维护本地 iface -> impl 映射关系
    }

    @Override
    public Bootstrap scan(String packageName) {
        // 1、扫描包下所有的class文件
        List<String> classNames = PackageUtil.traversal(packageName);
        // 2、反射过滤目标实现并对外发布
        classNames.stream().map(name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }).filter(clazz -> clazz.getAnnotation(XQ.class) != null).forEach(clazz -> {
            Object obj = null;
            try {
                obj = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
            for (Class<?> iface : clazz.getInterfaces()) {
                ProvideConfig<Object> config = new ProvideConfig<>(iface, obj);
                release(config);
            }
        });
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
                                    .addLast(new Decoder())
                                    .addLast(new Encoder())
                                    .addLast(new CompressCodec())
                                    .addLast(new SerializeCodec())
                                    .addLast(new MethodCallHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(config.getPort()).sync();
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
