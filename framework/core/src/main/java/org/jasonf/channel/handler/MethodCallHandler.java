package org.jasonf.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.ProviderBootstrap;
import org.jasonf.boot.ShutdownHook;
import org.jasonf.protection.RateLimiter;
import org.jasonf.protection.TokenBucketRateLimiter;
import org.jasonf.transfer.enumeration.MessageType;
import org.jasonf.transfer.message.Message;
import org.jasonf.transfer.message.Request;

import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

@Slf4j
public class MethodCallHandler extends SimpleChannelInboundHandler<Message> {
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final Map<SocketAddress, RateLimiter> RATE_LIMITER = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (ShutdownHook.BAFFLE.get()) {
            msg.setMessageType(MessageType.SERVICE_SHUTDOWN.getCode());
            ctx.channel().writeAndFlush(msg);
            return;
        }   // 请求不处理直接返回

        ShutdownHook.COUNTER.increment();   // before process request, counter add 1

        try {
            Request request = (Request) msg.getPayload();
            if (request != null) {
                /* -------------------------------------- 基于IP地址的限流检测 --------------------------------- */
                SocketAddress socketAddress = ctx.channel().remoteAddress();
                RateLimiter rateLimiter = RATE_LIMITER.get(socketAddress);
                if (rateLimiter == null) {
                    rateLimiter = new TokenBucketRateLimiter(500, 10);
                    RATE_LIMITER.put(socketAddress, rateLimiter);
                }
                if (!rateLimiter.allowRequest()) throw new RuntimeException("请求 [" + msg.getID() + "] 被限制");

                /* ------------------------------------------ 执行方法调用 ------------------------------------- */
                Object obj = ProviderBootstrap.SERVICES.get(request.getIface()).getImpl();
                Method method = obj.getClass().getDeclaredMethod(request.getMethod(), request.getParamType());
                Object rtn = method.invoke(obj, request.getParamValue());
                msg.setPayload(rtn);
            } else {
                // 心跳检测不受限制, 模拟心跳检测时不同节点的响应时间(25 ~ 80ms)
                Thread.sleep(25 + RANDOM.nextInt(56));
            }
            msg.setMessageType(MessageType.RESPONSE_SUCCESS.getCode());
        } catch (Exception ex) {
            log.error("请求 [{}] 处理发生异常", msg.getID(), ex);
            msg.setMessageType(MessageType.RESPONSE_FAILURE.getCode());
            msg.setPayload(null);   // 清除信息
        }
        ctx.channel().writeAndFlush(msg);

        ShutdownHook.COUNTER.decrement();   // after process request, counter add -1
    }
}
