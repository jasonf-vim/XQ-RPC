package org.jasonf.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.InvokerBootstrap;
import org.jasonf.transfer.enumeration.MessageType;
import org.jasonf.transfer.message.Message;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

@Slf4j
public class ReturnValueHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        byte code = msg.getMessageType();
        if (code == MessageType.RESPONSE_SUCCESS.getCode())
            InvokerBootstrap.PENDING_REQUEST.get(msg.getID()).complete(msg.getPayload());
        // 打印消息的接收状态
        for (MessageType messageType : MessageType.values()) {
            if (code == messageType.getCode()) {
                log.info("请求ID: [{}], 响应码: [{}], 状态: [{}]", msg.getID(), code, messageType.getDesc());
                break;
            }
        }
    }
}
