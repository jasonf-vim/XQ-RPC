package org.jasonf.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jasonf.ProviderBootstrap;
import org.jasonf.transfer.enumeration.MessageType;
import org.jasonf.transfer.message.Message;
import org.jasonf.transfer.message.Request;

import java.lang.reflect.Method;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

public class MethodCallHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Request request = (Request) msg.getPayload();
        if (request != null) {
            // 通过反射调用方法并获取返回值
            Object obj = ProviderBootstrap.SERVICES.get(request.getIface()).getImpl();
            Method method = obj.getClass().getDeclaredMethod(request.getMethod(), request.getParamType());
            Object rtn = method.invoke(obj, request.getParamValue());
            msg.setPayload(rtn);
            msg.setMessageType(MessageType.RESPONSE_SUCCESS.getCode());
        }   // 心跳检测不必改变消息类型
        ctx.channel().writeAndFlush(msg);
    }
}
