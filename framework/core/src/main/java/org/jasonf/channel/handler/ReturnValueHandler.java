package org.jasonf.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.InvokerBootstrap;
import org.jasonf.exception.ResponseException;
import org.jasonf.transfer.enumeration.MessageType;
import org.jasonf.transfer.message.Message;

import java.util.concurrent.CompletableFuture;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

@Slf4j
public class ReturnValueHandler extends SimpleChannelInboundHandler<Message> {
    private static final RuntimeException RESPONSE_EXCEPTION = new ResponseException();

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
        } else completableFuture.completeExceptionally(RESPONSE_EXCEPTION);
    }
}
