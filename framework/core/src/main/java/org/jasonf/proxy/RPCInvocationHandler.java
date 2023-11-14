package org.jasonf.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.InvokerBootstrap;
import org.jasonf.annotation.Retry;
import org.jasonf.exception.NetworkException;
import org.jasonf.netty.BootstrapHolder;
import org.jasonf.protection.CircuitBreaker;
import org.jasonf.transfer.enumeration.MessageType;
import org.jasonf.transfer.message.Message;
import org.jasonf.transfer.message.Request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

import static org.jasonf.transfer.Constant.*;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

@Slf4j
@Setter
public class RPCInvocationHandler implements InvocationHandler {
    private static final Map<InetSocketAddress, CircuitBreaker> CIRCUIT_BREAKER = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService DELAY_POOL = Executors.newScheduledThreadPool(BREAKER_POOL_SIZE);
    private String iface;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 1、封装请求
        Request request = Request.builder()
                .iface(iface)
                .method(method.getName())
                .paramType(method.getParameterTypes())
                .paramValue(args)
                .build();
        Message message = Message.builder()
                .messageType(MessageType.REQUEST.getCode())
                .serialType(InvokerBootstrap.getInstance().getConfig().getSerialize().getCode())
                .compressType(InvokerBootstrap.getInstance().getConfig().getCompress().getCode())
                .payload(request)
                .build();

        // 2、异常重试
        int times = 0, interval = 0;
        Retry retry = method.getAnnotation(Retry.class);
        if (retry != null) {
            times = retry.times();
            interval = retry.interval();
        }

        CircuitBreaker breaker = null;
        for (int i = 0; i <= times; i++) {
            try {
                long id = InvokerBootstrap.getInstance().getConfig().getIdGenerator().getUniqueID();
                message.setID(id);

                InvokerBootstrap.MESSAGE_ID_THREAD_LOCAL.set(id);   // 3、设置线程变量

                // 4、从负载均衡器获取服务节点
                InetSocketAddress serviceAddress = InvokerBootstrap.getInstance()
                        .getConfig().getLoadBalancer().getServiceAddress(iface);
                if (log.isDebugEnabled()) {
                    log.debug("node{ip: {}, port: {}}", serviceAddress.getHostString(), serviceAddress.getPort());
                }

                // 5、熔断检测
                breaker = CIRCUIT_BREAKER.get(serviceAddress);
                if (breaker == null) {
                    breaker = new CircuitBreaker(MAX_ERROR_COUNT, MIN_ERROR_COUNT, MAX_ERROR_RATE);
                    CIRCUIT_BREAKER.put(serviceAddress, breaker);
                }
                if (breaker.isBreak()) {
                    if (!breaker.isResetting()) {
                        breaker.setResetting(true);
                        DELAY_POOL.schedule(() -> {
                            CIRCUIT_BREAKER.get(serviceAddress).reset();
                        }, BREAK_TIME, TimeUnit.SECONDS);
                    }
                    throw new RuntimeException("当前链路 [" + serviceAddress + "] 已被熔断, 无法发送请求");
                }
                breaker.requestCount();    // 请求计数

                Channel channel = getAvailableChannel(serviceAddress);    // 6、与服务节点建立连接

                // 7、发送请求数据
                CompletableFuture<Object> resultFuture = new CompletableFuture<>();
                channel.writeAndFlush(message).addListener((ChannelFutureListener) promise -> {
                    if (!promise.isSuccess()) resultFuture.completeExceptionally(promise.cause());
                });
                InvokerBootstrap.PENDING_REQUEST.put(message.getID(), resultFuture);    // 挂起请求

                InvokerBootstrap.MESSAGE_ID_THREAD_LOCAL.remove();  // 8、清除线程变量

                // 9、等待结果返回
                return resultFuture.get(RESULT_TIMEOUT, TimeUnit.SECONDS);
            } catch (Exception ex) {
                if (breaker != null) breaker.errorCount();    // 异常请求计数
                if (i == times) break;
                log.error("方法 [{}] 调用发生异常, 稍后发起第 [{}] 次重试", method.getName(), i + 1, ex);
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    log.error("准备重试时发生异常", e);
                }
            }
        }
        throw new RuntimeException("方法 [" + method.getName() + "] 远程调用失败");
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
                channel = channelFuture.get(CHANNEL_TIMEOUT, TimeUnit.SECONDS);   // 阻塞等待
                InvokerBootstrap.CHANNEL_CACHE.put(address, channel);  // 缓存 channel
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                throw new NetworkException("获取 " + address + " 的 channel 时发生异常");
            }
        }
        return channel;
    }
}
