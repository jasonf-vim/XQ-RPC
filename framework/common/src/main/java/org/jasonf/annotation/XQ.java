package org.jasonf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author jasonf
 * @Date 2023/11/12
 * @Description 标记服务端需要暴露的实现
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XQ {
    String value() default "default";   // 分组信息
}
