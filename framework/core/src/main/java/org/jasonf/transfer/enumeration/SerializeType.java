package org.jasonf.transfer.enumeration;

import lombok.Getter;

/**
 * @Author jasonf
 * @Date 2023/11/7
 * @Description
 */

@Getter
public enum SerializeType {
    JDK((byte) 1, "jdk"),
    HESSIAN((byte) 2, "hessian");

    private byte code;
    private String desc;

    SerializeType(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
