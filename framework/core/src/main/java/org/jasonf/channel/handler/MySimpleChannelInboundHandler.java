package org.jasonf.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jasonf.InvokerBootstrap;

import java.nio.charset.Charset;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

public class MySimpleChannelInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {
        InvokerBootstrap.PENDING_REQUEST.get(1L).complete(msg.toString(Charset.defaultCharset()));
    }
}
