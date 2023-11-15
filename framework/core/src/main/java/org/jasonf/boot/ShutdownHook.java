package org.jasonf.boot;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Author jasonf
 * @Date 2023/11/15
 * @Description
 */

public class ShutdownHook extends Thread {
    public static final AtomicBoolean BAFFLE = new AtomicBoolean();   // init with false
    public static final LongAdder COUNTER = new LongAdder();  // init with zero

    @Override
    public void run() {
        BAFFLE.set(true);   // enable the baffle

        long start = System.currentTimeMillis();
        while (COUNTER.sum() != 0L && System.currentTimeMillis() - start < 10000L) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }   // wait up to ten sec
    }
}
