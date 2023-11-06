package org.jasonf.transfer.message;

import lombok.*;

import java.io.Serializable;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description 请求实体
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Request implements Serializable {
    private String iface;   // 接口全限定名
    private String method;  // 方法名
    private Class<?>[] paramType;   // 形参
    private Object[] paramValue;    // 实参
}
