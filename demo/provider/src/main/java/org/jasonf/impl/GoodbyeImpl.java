package org.jasonf.impl;

import org.jasonf.Goodbye;

/**
 * @Author jasonf
 * @Date 2023/11/12
 * @Description
 */

public class GoodbyeImpl implements Goodbye {
    @Override
    public void bye(String msg) {
        System.out.println("goodbye " + msg + "!");
    }
}
