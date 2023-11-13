package org.jasonf.generator;

/**
 * @Author jasonf
 * @Date 2023/11/13
 * @Description
 */

public interface IDGenerator {
    /**
     * 分布式系统中生成唯一ID
     *
     * @return ID
     */
    long getUniqueID();
}
