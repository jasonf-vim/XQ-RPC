package org.jasonf.heartbeat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.InvokerBootstrap;
import org.jasonf.transfer.enumeration.MessageType;
import org.jasonf.transfer.message.Message;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

import static org.jasonf.transfer.Constant.*;

/**
 * @Author jasonf
 * @Date 2023/11/10
 * @Description 心跳检测器
 */

public class HeartbeatDetector {
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService CACHED_POOL = Executors.newCachedThreadPool();

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void start() {
        // 定时发起心跳检测任务
        SCHEDULED_POOL.scheduleAtFixedRate(new Detection(), 0L, INTERVAL, TimeUnit.SECONDS);
    }

    private static class Detection implements Runnable {
        @Override
        public void run() {
            for (Map.Entry<InetSocketAddress, Channel> entry : InvokerBootstrap.CHANNEL_CACHE.entrySet()) {
                CACHED_POOL.execute(new Measure(entry));    // 同时检测多个 channel
            }
        }
    }

    @Slf4j
    private static class Measure implements Runnable {
        Map.Entry<InetSocketAddress, Channel> entry;
        Message msg = Message.builder()
                .messageType(MessageType.HEART_BEAT.getCode())
                .serialType(InvokerBootstrap.getInstance().getConfig().getSerialize().getCode())
                .compressType(InvokerBootstrap.getInstance().getConfig().getCompress().getCode())
                .build();

        public Measure(Map.Entry<InetSocketAddress, Channel> entry) {
            this.entry = entry;
        }

        @Override
        public void run() {
            InetSocketAddress address = entry.getKey();
            Channel channel = entry.getValue();
            long time = -1L;
            for (int i = 0; i < RETRY; i++) {
                time = probe(channel);
                if (time < 0) {
                    try {
                        // 避免大面积同时重试, 产生重试风暴
                        Thread.sleep(RANDOM.nextInt(WAIT_TIME));
                    } catch (InterruptedException ex) {
                        log.error("心跳检测重试时发生异常: ", ex);
                    }
                } else break;
            }
            if (time < 0) {
                InvokerBootstrap.CHANNEL_CACHE.remove(address);
                InvokerBootstrap.RESPONSE_TIME.remove(address);
                log.info("检测到节点 [{}] 离线, 已将其从缓存中移除", address);
            } else {
                InvokerBootstrap.RESPONSE_TIME.put(address, time);
                log.info("检测到节点 [{}] 在线, 响应时间 [{}] ms", address, time);
            }
        }

        public long probe(Channel channel) {
            msg.setID(InvokerBootstrap.getInstance().getConfig().getIdGenerator().getUniqueID());    // 重置消息ID
            CompletableFuture<Object> resultFuture = new CompletableFuture<>();
            long start = System.currentTimeMillis(), end;
            channel.writeAndFlush(msg).addListener((ChannelFutureListener) promise -> {
                if (!promise.isSuccess()) resultFuture.completeExceptionally(promise.cause());
            });
            InvokerBootstrap.PENDING_REQUEST.put(msg.getID(), resultFuture);    // 挂起请求
            try {
                resultFuture.get(TIMEOUT, TimeUnit.MILLISECONDS);
                end = System.currentTimeMillis();
                return end - start;    // 返回发出消息到接收消息的时间差
            } catch (ExecutionException | InterruptedException | TimeoutException ex) {
                return -1L;
            }
        }
    }
}
