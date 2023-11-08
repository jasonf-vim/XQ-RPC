package org.jasonf.serialize;

/**
 * @Author jasonf
 * @Date 2023/11/7
 * @Description
 */

public interface Serializer {
    byte[] serialize(Object obj, long id);

    Object deserialize(byte[] bytes, long id);
}
