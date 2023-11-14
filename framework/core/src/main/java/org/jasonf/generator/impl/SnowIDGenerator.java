package org.jasonf.generator.impl;

import org.jasonf.generator.IDGenerator;

import java.util.concurrent.atomic.LongAdder;

import static org.jasonf.Constant.*;

/**
 * <pre>
 *     +--------+--------+---------+----------+
 *     |  机房  |  机器   | 时间戳  |  序列号   |
 *     +--------+--------+---------+----------+
 *     |   5    |   5    |   42    |    12    |
 *     +--------+--------+---------+----------+
 * </pre>
 *
 * @Author jasonf
 * @Date 2023/11/7
 * @Description ID生成器, 雪花算法
 */

public class SnowIDGenerator implements IDGenerator {
    private final long LOCATE_ID;
    private long lastTimestamp = -1L;
    private final LongAdder SEQUENCE = new LongAdder();     // 线程安全

    public SnowIDGenerator(long dataCenterId, long machineId) {
        if (dataCenterId < 0 || dataCenterId > DATA_CENTER_MAX) throw new RuntimeException("数据中心ID不合法");
        if (machineId < 0 || machineId > MACHINE_MAX) throw new RuntimeException("服务器ID不合法");
        LOCATE_ID = dataCenterId << DATA_CENTER_LEFT | machineId << MACHINE_LEFT;
    }

    @Override
    public long getUniqueID() {
        long timestamp = System.currentTimeMillis() - INITIAL_TIMESTAMP;    // 相对起始时间的偏移量
        if (timestamp < lastTimestamp) throw new RuntimeException("服务器时钟回拨");
        if (timestamp == lastTimestamp) {
            SEQUENCE.increment();
            // 若超过 SEQUENCE 上限则强制延迟到下一时间
            if (SEQUENCE.sum() > SEQUENCE_MAX) {
                timestamp = getNextTimestamp();
                SEQUENCE.reset();
            }
        } else SEQUENCE.reset();    // sequence 从属于不同的 timestamp, 逻辑上简洁一致
        lastTimestamp = timestamp;  // 更新上一次获取ID的时间戳
        return LOCATE_ID | timestamp << TIMESTAMP_LEFT | SEQUENCE.sum();
    }

    private long getNextTimestamp() {
        long timestamp;
        do {
            timestamp = System.currentTimeMillis() - INITIAL_TIMESTAMP;
        } while (timestamp == lastTimestamp);
        return timestamp;
    }
}
