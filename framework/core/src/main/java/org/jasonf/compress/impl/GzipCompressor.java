package org.jasonf.compress.impl;

import lombok.extern.slf4j.Slf4j;
import org.jasonf.compress.Compressor;
import org.jasonf.exception.MessageDecodeException;
import org.jasonf.exception.MessageEncodeException;
import org.jasonf.transfer.Constant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Author jasonf
 * @Date 2023/11/8
 * @Description
 */

@Slf4j
public class GzipCompressor implements Compressor {
    private static volatile Compressor compressor;

    private GzipCompressor() {
    }

    public static Compressor getInstance() {
        if (compressor == null) {
            synchronized (GzipCompressor.class) {
                if (compressor == null) {
                    compressor = new GzipCompressor();
                }
            }
        }
        return compressor;
    }

    @Override
    public byte[] compress(byte[] source, long id) {
        if (source == null) return null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
            gzip.write(source);
            gzip.finish();
            return baos.toByteArray();
        } catch (IOException ex) {
            log.error("压缩请求 [{}] 时发生异常", id);
            throw new MessageEncodeException(ex);
        }
    }

    @Override
    public byte[] decompress(byte[] source, long id) {
        if (source == null) return null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ByteArrayInputStream bais = new ByteArrayInputStream(source);
             GZIPInputStream gunzip = new GZIPInputStream(bais)) {
            byte[] buf = new byte[Constant.BUFFER_SIZE];
            int len;
            while ((len = gunzip.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            return baos.toByteArray();
        } catch (IOException ex) {
            log.error("解压请求 [{}] 时发生异常", id);
            throw new MessageDecodeException(ex);
        }
    }
}
