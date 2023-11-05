package org.jasonf;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

public class HelloWorld implements Hello {
    @Override
    public void greet(String msg) {
        System.out.println("hello " + msg);
    }
}
