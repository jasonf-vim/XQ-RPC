package org.jasonf;

import org.jasonf.annotation.Retry;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

public interface Hello {
    @Retry(times = 5, interval = 1000)
    String greet(String msg);
}
