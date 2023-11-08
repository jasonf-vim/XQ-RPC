package org.jasonf.serialize.impl;

import lombok.extern.slf4j.Slf4j;
import org.jasonf.exception.MessageDecodeException;
import org.jasonf.exception.MessageEncodeException;
import org.jasonf.serialize.Serializer;

import java.io.*;

/**
 * @Author jasonf
 * @Date 2023/11/7
 * @Description
 */

@Slf4j
public class JDKSerializer implements Serializer {
    private static volatile Serializer serializer;

    private JDKSerializer() {
    }

    public static Serializer getInstance() {
        if (serializer == null) {
            synchronized (JDKSerializer.class) {
                if (serializer == null) {
                    serializer = new JDKSerializer();
                }
            }
        }
        return serializer;
    }

    @Override
    public byte[] serialize(Object obj, long id) {
        if (obj == null) return null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException ex) {
            log.error("序列化请求 [{}] 时发生异常", id);
            throw new MessageEncodeException(ex);
        }
    }

    @Override
    public Object deserialize(byte[] bytes, long id) {
        if (bytes == null) return null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            log.error("反序列化请求 [{}] 时发生异常", id);
            throw new MessageDecodeException(ex);
        }
    }
}
