package org.jasonf.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.jasonf.compress.Compressor;
import org.jasonf.compress.impl.GzipCompressor;
import org.jasonf.exception.MessageDecodeException;
import org.jasonf.exception.MessageEncodeException;
import org.jasonf.transfer.enumeration.CompressorType;
import org.jasonf.transfer.message.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author jasonf
 * @Date 2023/11/7
 * @Description
 */

public class CompressCodec extends MessageToMessageCodec<Message, Message> {
    private static final Map<Byte, Compressor> COMPRESSOR_CACHE = new ConcurrentHashMap<>();

    static {
        COMPRESSOR_CACHE.put(CompressorType.GZIP.getCode(), GzipCompressor.getInstance());
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        Compressor compressor = COMPRESSOR_CACHE.get(msg.getCompressType());
        if (compressor == null) throw new MessageEncodeException("获取压缩器 " + msg.getCompressType() + "失败");
        msg.setPayload(compressor.compress((byte[]) msg.getPayload(), msg.getID()));
        out.add(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        Compressor compressor = COMPRESSOR_CACHE.get(msg.getCompressType());
        if (compressor == null) throw new MessageDecodeException("获取解压器 " + msg.getCompressType() + "失败");
        msg.setPayload(compressor.decompress((byte[]) msg.getPayload(), msg.getID()));
        out.add(msg);
    }
}
