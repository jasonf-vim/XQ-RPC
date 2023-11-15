package org.jasonf.impl;

import org.jasonf.Hello;
import org.jasonf.annotation.XQ;

/**
 * @Author jasonf
 * @Date 2023/11/15
 * @Description
 */

@XQ("major")
public class HelloSpring implements Hello {
    @Override
    public String greet(String msg) {
        return "hello " + msg + " from spring";
    }
}
