package org.jasonf.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.exception.MessageDecodeException;
import org.jasonf.exception.MessageEncodeException;
import org.jasonf.transfer.enumeration.MessageType;
import org.jasonf.transfer.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Author jasonf
 * @Date 2023/11/7
 * @Description
 */

@Slf4j
public class CompressCodec extends MessageToMessageCodec<Message, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        if (msg.getMessageType() != MessageType.HEART_BEAT.getCode()) {
            byte[] payload = (byte[]) msg.getPayload();
            try (ByteArrayOutputStream bao = new ByteArrayOutputStream();
                 GZIPOutputStream gzip = new GZIPOutputStream(bao)) {
                gzip.write(payload);
                gzip.finish();
                msg.setPayload(bao.toByteArray());
            } catch (IOException ex) {
                log.error("压缩请求 [{}] 时发生异常", msg.getID());
                throw new MessageEncodeException(ex);
            }
        }   // 心跳检测不需要压缩
        out.add(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        if (msg.getMessageType() != MessageType.HEART_BEAT.getCode()) {
            byte[] payload = (byte[]) msg.getPayload();
            try (ByteArrayOutputStream bao = new ByteArrayOutputStream();
                 ByteArrayInputStream bai = new ByteArrayInputStream(payload);
                 GZIPInputStream gunzip = new GZIPInputStream(bai)) {
                byte[] buf = new byte[128];
                int len;
                while ((len = gunzip.read(buf)) != -1) {
                    bao.write(buf, 0, len);
                }
                msg.setPayload(bao.toByteArray());
            } catch (IOException ex) {
                log.error("解压请求 [{}] 时发生异常", msg.getID());
                throw new MessageDecodeException(ex);
            }
        }   // 心跳检测不需要解压
        out.add(msg);
    }
}
