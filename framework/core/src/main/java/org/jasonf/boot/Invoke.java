package org.jasonf.boot;

import org.jasonf.config.InvokeConfig;
import org.jasonf.exception.InvocationNotSupportedException;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public interface Invoke {
    default void retrieval(InvokeConfig<?> config) {
        throw new InvocationNotSupportedException("当前方法调用不受支持");
    }
}
