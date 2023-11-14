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

    public static final long RESULT_TIMEOUT = 3L;   // 等待结果返回的超时时间
    public static final long CHANNEL_TIMEOUT = 3L;   // 获取 channel 的最长等待时间

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

    public static final long INTERVAL = 3L;    // 心跳检测的间隔时间
    public static final int RETRY = 3;    // 心跳检测最多尝试次数
    public static final long TIMEOUT = 200L;    // 心跳检测最长等待时间
    public static final int WAIT_TIME = 51;     // 心跳检测重试最长等待时间

    public static final int BREAKER_POOL_SIZE = 5;    // 熔断器重置任务池大小
    public static final int BREAK_TIME = 30;    // 熔断时间

    // 熔断器
    public static final int MAX_ERROR_COUNT = 500;
    public static final int MIN_ERROR_COUNT = 80;
    public static final float MAX_ERROR_RATE = 0.8F;
}
