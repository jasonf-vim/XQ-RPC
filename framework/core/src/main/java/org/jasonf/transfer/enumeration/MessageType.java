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
    HEART_BEAT((byte) 0, "心跳检测");

    private byte id;
    private String desc;

    MessageType(byte id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
