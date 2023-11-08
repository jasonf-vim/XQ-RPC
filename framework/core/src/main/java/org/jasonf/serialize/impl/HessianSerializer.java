package org.jasonf.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
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
public class HessianSerializer implements Serializer {
    private static volatile Serializer serializer;

    private HessianSerializer() {
    }

    public static Serializer getInstance() {
        if (serializer == null) {
            synchronized (HessianSerializer.class) {
                if (serializer == null) {
                    serializer = new HessianSerializer();
                }
            }
        }
        return serializer;
    }

    @Override
    public byte[] serialize(Object obj, long id) {
        if (obj == null) return null;
        ByteArrayOutputStream baos = null;
        Hessian2Output output = null;
        try {
            baos = new ByteArrayOutputStream();
            output = new Hessian2Output(baos);
            output.writeObject(obj);
            output.flush();
        } catch (IOException ex) {
            log.error("序列化请求 [{}] 时发生异常", id);
            throw new MessageEncodeException(ex);
        } finally {
            try {
                if (output != null) output.close();
                if (output != null) baos.close();
            } catch (IOException ex) {
                log.error("关闭流时发生异常", ex);
            }
        }
        return baos.toByteArray();
    }

    @Override
    public Object deserialize(byte[] bytes, long id) {
        if (bytes == null) return null;
        ByteArrayInputStream bais = null;
        Hessian2Input input = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            input = new Hessian2Input(bais);
            return input.readObject();
        } catch (IOException ex) {
            log.error("反序列化请求 [{}] 时发生异常", id);
            throw new MessageDecodeException(ex);
        } finally {
            try {
                if (input != null) input.close();
                if (bais != null) bais.close();
            } catch (IOException ex) {
                log.error("关闭流时发生异常", ex);
            }
        }
    }
}
