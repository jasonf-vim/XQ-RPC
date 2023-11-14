package org.jasonf.impl;

import org.jasonf.Hello;
import org.jasonf.annotation.XQ;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

@XQ("master")
public class HelloWorld implements Hello {
    @Override
    public String greet(String msg) {
        return "Hello " + msg + "!";
    }
}
