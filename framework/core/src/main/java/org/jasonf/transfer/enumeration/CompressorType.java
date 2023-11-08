package org.jasonf.transfer.enumeration;

import lombok.Getter;

/**
 * @Author jasonf
 * @Date 2023/11/7
 * @Description
 */

@Getter
public enum CompressorType {
    GZIP((byte) 1, "gzip");

    private byte code;
    private String desc;

    CompressorType(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
