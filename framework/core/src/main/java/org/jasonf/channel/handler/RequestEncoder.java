package org.jasonf.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.exception.MessageEncodeException;
import org.jasonf.transfer.enumeration.MessageType;
import org.jasonf.transfer.message.Message;
import org.jasonf.transfer.message.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
public class RequestEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(MAGIC);
        byteBuf.writeByte(VERSION);
        byteBuf.writeShort(HEADER_LENGTH);

        // 记录偏移量, 写指针暂时向后略过 full length 字段
        int prevOffset = byteBuf.writerIndex();
        byteBuf.writerIndex(prevOffset + FULL_FIELD_LENGTH);

        byteBuf.writeByte(message.getMessageType());
        byteBuf.writeByte(message.getSerialType());
        byteBuf.writeByte(message.getCompressType());
        byteBuf.writeLong(message.getID());

        byte[] request = new byte[]{};
        if (message.getMessageType() == MessageType.REQUEST.getId()) {
            request = serializeRequest((Request) message.getPayload());
            // todo 压缩请求报文
        }
        byteBuf.writeBytes(request);

        // 回写请求报文总长度, 并恢复写指针
        int currOffset = byteBuf.writerIndex();
        byteBuf.writerIndex(prevOffset);
        byteBuf.writeInt(HEADER_LENGTH + request.length);
        byteBuf.writerIndex(currOffset);
    }

    private byte[] serializeRequest(Request request) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(request);
            return bos.toByteArray();
        } catch (IOException ex) {
            log.error("序列化请求 [{}] 时发生异常", request);
            throw new MessageEncodeException(ex);
        }
    }
}
