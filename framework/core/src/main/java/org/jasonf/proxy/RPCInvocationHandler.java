package org.jasonf.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.InvokerBootstrap;
import org.jasonf.exception.NetworkException;
import org.jasonf.transfer.message.Message;
import org.jasonf.transfer.message.Request;
import org.jasonf.netty.BootstrapHolder;
import org.jasonf.registry.Registry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

@Slf4j
public class RPCInvocationHandler implements InvocationHandler {
    private Registry registry;
    private String iface;

    public RPCInvocationHandler(Registry registry, String iface) {
        this.registry = registry;
        this.iface = iface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1、从注册中心拉取服务列表
        List<InetSocketAddress> nodes = registry.detect(iface);
        InetSocketAddress node = nodes.get(0);
        if (log.isDebugEnabled()) {
            log.debug("node{ip: {}, port: {}}", node.getHostString(), node.getPort());
        }
        // todo 本地缓存服务列表，并添加watcher及时维护
        // todo 支持不同服务调用策略（负载均衡）
        Channel channel = getAvailableChannel(node);    // 2、与服务节点建立连接

        // 3、封装请求
        Request request = Request.builder()
                .iface(iface)
                .method(method.getName())
                .paramType(method.getParameterTypes())
                .paramValue(args)
                .build();
        Message message = Message.builder()
                .ID(InvokerBootstrap.ID_GENERATOR.getUniqueID())
                .messageType((byte) 1)
                .serialType((byte) 1)
                .compressType((byte) 1)
                .payload(request)
                .build();

        // 4、发送请求数据
        CompletableFuture<Object> resultFuture = new CompletableFuture<>();
        channel.writeAndFlush(message).addListener((ChannelFutureListener) promise -> {
            if (!promise.isSuccess()) resultFuture.completeExceptionally(promise.cause());
        });
        InvokerBootstrap.PENDING_REQUEST.put(message.getID(), resultFuture);    // 挂起请求

        // 5、等待结果返回
        return resultFuture.get(3, TimeUnit.SECONDS);
    }

    private Channel getAvailableChannel(InetSocketAddress address) {
        // 优先尝试从缓存中获取 channel
        Channel channel = InvokerBootstrap.CHANNEL_CACHE.get(address);
        if (channel == null) {
            // 采用 CompletableFuture 异步获取 channel, 其用法类似 Callable
            CompletableFuture<Channel> channelFuture = new CompletableFuture<>();
            BootstrapHolder.getBootstrap().connect(address).addListener((ChannelFutureListener) promise -> {
                if (promise.isSuccess()) {
                    channelFuture.complete(promise.channel());
                    if (log.isDebugEnabled()) log.debug("成功和 [{}] 建立连接", address);
                } else if (!promise.isDone()) channelFuture.completeExceptionally(promise.cause());
            });
            try {
                channel = channelFuture.get(3, TimeUnit.SECONDS);   // 阻塞等待
                InvokerBootstrap.CHANNEL_CACHE.put(address, channel);  // 缓存 channel
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                throw new NetworkException("获取 [{}] 的 channel 时发生异常");
            }
        }
        return channel;
    }
}
