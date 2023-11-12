package org.jasonf.registry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public interface Registry {
    /**
     * 注册服务
     *
     * @param iface 服务接口全限定名
     */
    void register(String iface);

    /**
     * 发现服务
     *
     * @param iface 调用接口全限定名
     * @return 可提供服务的节点列表
     */
    List<InetSocketAddress> detect(String iface);

    /**
     * 断开连接
     */
    void disconnect();
}
