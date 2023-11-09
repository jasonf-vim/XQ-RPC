package org.jasonf.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.InvokerBootstrap;
import org.jasonf.exception.NetworkException;
import org.jasonf.netty.BootstrapHolder;
import org.jasonf.transfer.enumeration.CompressorType;
import org.jasonf.transfer.enumeration.MessageType;
import org.jasonf.transfer.enumeration.SerializeType;
import org.jasonf.transfer.message.Message;
import org.jasonf.transfer.message.Request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
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
@Setter
public class RPCInvocationHandler implements InvocationHandler {
    private String iface;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1、封装请求
        Request request = Request.builder()
                .iface(iface)
                .method(method.getName())
                .paramType(method.getParameterTypes())
                .paramValue(args)
                .build();
        Message message = Message.builder()
                .ID(InvokerBootstrap.ID_GENERATOR.getUniqueID())
                .messageType(MessageType.REQUEST.getCode())
                .serialType(SerializeType.HESSIAN.getCode())
                .compressType(CompressorType.GZIP.getCode())
                .payload(request)
                .build();

        InvokerBootstrap.MESSAGE_ID_THREAD_LOCAL.set(message.getID());

        // 2、从负载均衡器获取服务节点
        InetSocketAddress serviceAddress = InvokerBootstrap.loadBalancer.getServiceAddress(iface);
        if (log.isDebugEnabled()) {
            log.debug("node{ip: {}, port: {}}", serviceAddress.getHostString(), serviceAddress.getPort());
        }
        // todo 本地缓存服务列表，并添加watcher及时维护
        Channel channel = getAvailableChannel(serviceAddress);    // 3、与服务节点建立连接

        // 4、发送请求数据
        CompletableFuture<Object> resultFuture = new CompletableFuture<>();
        channel.writeAndFlush(message).addListener((ChannelFutureListener) promise -> {
            if (!promise.isSuccess()) resultFuture.completeExceptionally(promise.cause());
        });
        InvokerBootstrap.PENDING_REQUEST.put(message.getID(), resultFuture);    // 挂起请求

        InvokerBootstrap.MESSAGE_ID_THREAD_LOCAL.remove();

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
