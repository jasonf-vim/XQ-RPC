package org.jasonf.transfer.enumeration;

import lombok.Getter;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

@Getter
public enum MessageType {
    REQUEST((byte) 1, "请求"),
    HEART_BEAT((byte) 0, "心跳检测"),
    RESPONSE_SUCCESS((byte) -1, "响应（成功）"),
    RESPONSE_FAILURE((byte) -128, "响应（失败）");

    private byte code;
    private String desc;

    MessageType(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
