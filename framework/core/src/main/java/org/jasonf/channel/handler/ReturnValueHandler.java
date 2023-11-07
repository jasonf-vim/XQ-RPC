package org.jasonf.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jasonf.InvokerBootstrap;
import org.jasonf.transfer.message.Message;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

public class ReturnValueHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        InvokerBootstrap.PENDING_REQUEST.get(msg.getID()).complete(msg.getPayload());
    }
}
