package org.jasonf.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.transfer.message.Message;

import static org.jasonf.transfer.Constant.*;

/**
 * <pre>
 *     0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17   18   19   20   21   22
 *     +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
 *     |       magic       |ver |head len |    full length    | mt |ser |comp|              message ID               |
 *     +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
 *     |                                                                                                             |
 *     |                                                    body                                                     |
 *     |                                                                                                             |
 *     +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
 * </pre>
 *
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

@Slf4j
public class Encoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(MAGIC);
        byteBuf.writeByte(VERSION);
        byteBuf.writeShort(HEADER_LENGTH);
        byte[] payload = (byte[]) message.getPayload();     // 序列化、压缩后的有效载荷
        byteBuf.writeInt(HEADER_LENGTH + payload.length);
        byteBuf.writeByte(message.getMessageType());
        byteBuf.writeByte(message.getSerialType());
        byteBuf.writeByte(message.getCompressType());
        byteBuf.writeLong(message.getID());
        byteBuf.writeBytes(payload);
    }
}
