package org.jasonf.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.jasonf.exception.MessageDecodeException;
import org.jasonf.exception.MessageEncodeException;
import org.jasonf.serialize.Serializer;
import org.jasonf.serialize.impl.HessianSerializer;
import org.jasonf.serialize.impl.JDKSerializer;
import org.jasonf.transfer.enumeration.SerializeType;
import org.jasonf.transfer.message.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author jasonf
 * @Date 2023/11/7
 * @Description
 */

public class SerializeCodec extends MessageToMessageCodec<Message, Message> {
    private static final Map<Byte, Serializer> SERIALIZER_CACHE = new ConcurrentHashMap<>();

    static {
        SERIALIZER_CACHE.put(SerializeType.JDK.getCode(), JDKSerializer.getInstance());
        SERIALIZER_CACHE.put(SerializeType.HESSIAN.getCode(), HessianSerializer.getInstance());
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        Serializer serializer = SERIALIZER_CACHE.get(msg.getSerialType());
        if (serializer == null) throw new MessageEncodeException("获取序列化器 " + msg.getSerialType() + "失败");
        msg.setPayload(serializer.serialize(msg.getPayload(), msg.getID()));
        out.add(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        Serializer serializer = SERIALIZER_CACHE.get(msg.getSerialType());
        if (serializer == null) throw new MessageDecodeException("获取反序列化器 " + msg.getSerialType() + "失败");
        msg.setPayload(serializer.deserialize((byte[]) msg.getPayload(), msg.getID()));
        out.add(msg);
    }
}
