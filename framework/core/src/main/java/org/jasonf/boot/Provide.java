package org.jasonf.boot;

import org.jasonf.config.ProvideConfig;
import org.jasonf.exception.InvocationNotSupportedException;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public interface Provide {
    default void release(ProvideConfig<?> config) {
        throw new InvocationNotSupportedException("当前方法调用不受支持");
    }

    default Bootstrap scan(String packageName) {
        throw new InvocationNotSupportedException("当前方法调用不受支持");
    }
}
