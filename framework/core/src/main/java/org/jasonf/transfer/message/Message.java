package org.jasonf.transfer.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description 封装请求和响应
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private Long ID;    // 消息唯一ID

    private byte messageType;   // 消息类型
    private byte serialType;    // 序列化类型
    private byte compressType;  // 压缩类型

    private Object payload;     // 消息实体
}
