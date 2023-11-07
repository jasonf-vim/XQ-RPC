package org.jasonf.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.exception.MessageDecodeException;
import org.jasonf.transfer.enumeration.MessageType;
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
public class Decoder extends LengthFieldBasedFrameDecoder {
    private static final int OFFSET = MAGIC_FIELD_LENGTH + VERSION_FIELD_LENGTH + HEADER_FIELD_LENGTH;

    public Decoder() {
        super(MAX_FRAME_LENGTH, OFFSET, FULL_FIELD_LENGTH, -(OFFSET + FULL_FIELD_LENGTH), 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        /* ------------------------------------------ 报文校验 ------------------------------------------ */
        Object frame = super.decode(ctx, in);    // 帧生成
        if (!(frame instanceof ByteBuf)) throw new MessageDecodeException("无法解析报文");
        ByteBuf byteBuf = (ByteBuf) frame;

        // 魔数值
        byte[] magic = new byte[MAGIC_FIELD_LENGTH];
        byteBuf.readBytes(magic);
        for (int i = 0; i < magic.length; i++) {
            if (magic[i] != MAGIC[i]) throw new MessageDecodeException("魔数值不匹配");
        }
        // 版本号
        byte version = byteBuf.readByte();
        if (version > VERSION) throw new MessageDecodeException("版本不兼容");

        /* ------------------------------------------ 解析报文 ------------------------------------------ */
        Message message = new Message();
        short headerLength = byteBuf.readShort();
        int fullLength = byteBuf.readInt();
        byte msgType = byteBuf.readByte();
        message.setMessageType(msgType);
        message.setSerialType(byteBuf.readByte());
        message.setCompressType(byteBuf.readByte());
        message.setID(byteBuf.readLong());
        // 心跳检测可直接返回
        if (msgType == MessageType.HEART_BEAT.getId()) return message;

        byte[] payload = new byte[fullLength - headerLength];
        byteBuf.readBytes(payload);
        message.setPayload(payload);
        return message;
    }
}
