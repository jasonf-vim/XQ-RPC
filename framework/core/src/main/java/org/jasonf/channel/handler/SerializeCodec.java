package org.jasonf.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.exception.MessageDecodeException;
import org.jasonf.exception.MessageEncodeException;
import org.jasonf.transfer.enumeration.MessageType;
import org.jasonf.transfer.message.Message;

import java.io.*;
import java.util.List;

/**
 * @Author jasonf
 * @Date 2023/11/7
 * @Description
 */

@Slf4j
public class SerializeCodec extends MessageToMessageCodec<Message, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        if (msg.getMessageType() != MessageType.HEART_BEAT.getId()) {
            Object payload = msg.getPayload();
            try (ByteArrayOutputStream bao = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bao)) {
                oos.writeObject(payload);
                oos.flush();
                msg.setPayload(bao.toByteArray());
            } catch (IOException ex) {
                log.error("序列化请求 [{}] 时发生异常", msg.getID());
                throw new MessageEncodeException(ex);
            }
        }   // 心跳检测不需要序列化
        out.add(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        if (msg.getMessageType() != MessageType.HEART_BEAT.getId()) {
            byte[] payload = (byte[]) msg.getPayload();
            try (ByteArrayInputStream bai = new ByteArrayInputStream(payload);
                 ObjectInputStream ois = new ObjectInputStream(bai)) {
                msg.setPayload(ois.readObject());
            } catch (IOException | ClassNotFoundException ex) {
                log.error("反序列化请求 [{}] 时发生异常", msg.getID());
                throw new MessageDecodeException(ex);
            }
        }   // 心跳检测不需要反序列化
        out.add(msg);
    }
}
