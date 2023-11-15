package org.jasonf.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.InvokerBootstrap;
import org.jasonf.exception.ResponseException;
import org.jasonf.loadbalance.AbstractLoadBalancer;
import org.jasonf.registry.Registry;
import org.jasonf.transfer.enumeration.MessageType;
import org.jasonf.transfer.message.Message;
import org.jasonf.transfer.message.Request;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

@Slf4j
public class ReturnValueHandler extends SimpleChannelInboundHandler<Message> {
    private static final RuntimeException RESPONSE_EXCEPTION = new ResponseException();
    private static final Registry REGISTRY = InvokerBootstrap.getInstance().getConfig().getRegistry();
    private static final AbstractLoadBalancer LOAD_BALANCER = InvokerBootstrap.getInstance().getConfig().getLoadBalancer();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        byte code = msg.getMessageType();
        // 打印消息的接收状态
        for (MessageType messageType : MessageType.values()) {
            if (code == messageType.getCode()) {
                log.info("请求ID: [{}], 响应码: [{}], 状态: [{}]", msg.getID(), code, messageType.getDesc());
                break;
            }
        }
        CompletableFuture<Object> completableFuture = InvokerBootstrap.PENDING_REQUEST.get(msg.getID());
        if (code == MessageType.RESPONSE_SUCCESS.getCode()) {
            completableFuture.complete(msg.getPayload());
        } else if (code == MessageType.SERVICE_SHUTDOWN.getCode()) {
            /* --------------------------------- 清除相关缓存 --------------------------------- */
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            InvokerBootstrap.CHANNEL_CACHE.remove(socketAddress);
            InvokerBootstrap.RESPONSE_TIME.remove(socketAddress);

            /* -------------- 更新负载均衡器的可用列表, 排除即将关闭服务的 address --------------- */
            String iface = ((Request) msg.getPayload()).getIface();
            List<InetSocketAddress> serviceList = REGISTRY.detect(iface);
            serviceList.remove(socketAddress);
            LOAD_BALANCER.reLoadBalance(iface, serviceList);

            completableFuture.completeExceptionally(RESPONSE_EXCEPTION);
        } else completableFuture.completeExceptionally(RESPONSE_EXCEPTION);
    }
}
