package org.jasonf.compress;

/**
 * @Author jasonf
 * @Date 2023/11/8
 * @Description
 */

public interface Compressor {
    byte[] compress(byte[] source, long id);

    byte[] decompress(byte[] source, long id);
}
