package org.jasonf.transfer;

import java.nio.charset.StandardCharsets;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

public class Constant {
    public static final String MAGIC_NAME = "XRPC";
    public static final byte[] MAGIC = MAGIC_NAME.getBytes(StandardCharsets.UTF_8);   // 魔数值

    public static final int VERSION = 1;    // 版本号

    public static final int BUFFER_SIZE = 128;

    // 私有协议各个头部字段的长度
    public static final int MAGIC_FIELD_LENGTH = 4;
    public static final int VERSION_FIELD_LENGTH = 1;
    public static final int HEADER_FIELD_LENGTH = 2;
    public static final int FULL_FIELD_LENGTH = 4;
    public static final int MESSAGE_TYPE_FIELD_LENGTH = 1;
    public static final int SERIALIZE_TYPE_FIELD_LENGTH = 1;
    public static final int COMPRESS_TYPE_FIELD_LENGTH = 1;
    public static final int MESSAGE_ID_FIELD_LENGTH = 8;

    // 头部字段总长度
    public static final int HEADER_LENGTH = MAGIC_FIELD_LENGTH + VERSION_FIELD_LENGTH + HEADER_FIELD_LENGTH +
            FULL_FIELD_LENGTH + MESSAGE_TYPE_FIELD_LENGTH + SERIALIZE_TYPE_FIELD_LENGTH +
            COMPRESS_TYPE_FIELD_LENGTH + MESSAGE_ID_FIELD_LENGTH;

    public static final int MAX_FRAME_LENGTH = 1024 * 1024;     // 最大帧长度 1MB
}
