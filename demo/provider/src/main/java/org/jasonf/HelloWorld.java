package org.jasonf;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

public class HelloWorld implements Hello {
    @Override
    public String greet(String msg) {
        return "Hello " + msg + "!";
    }
}
