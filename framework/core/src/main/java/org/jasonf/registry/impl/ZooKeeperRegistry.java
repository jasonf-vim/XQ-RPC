package org.jasonf.registry.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.jasonf.Constant;
import org.jasonf.InvokerBootstrap;
import org.jasonf.ProviderBootstrap;
import org.jasonf.exception.ServiceNotFoundException;
import org.jasonf.registry.Registry;
import org.jasonf.util.NetworkUtil;
import org.jasonf.util.ZooKeeperUtil;
import org.jasonf.watcher.ServiceMutation;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

@Slf4j
public class ZooKeeperRegistry implements Registry {
    private ZooKeeper zooKeeper;

    public ZooKeeperRegistry(String connect, int timeout) {
        zooKeeper = ZooKeeperUtil.getZooKeeper(connect, timeout);
    }

    @Override
    public void register(String iface, String group) {
        // 创建服务节点(/rpc/providers/org.jasonf.HelloWorld)
        String servPath = Constant.PROVIDERS_ROOT_PATH + "/" + iface;
        if (!ZooKeeperUtil.exists(zooKeeper, servPath))
            ZooKeeperUtil.create(zooKeeper, servPath, CreateMode.PERSISTENT);   // 持久节点
        // 创建分组节点(/rpc/providers/org.jasonf.HelloWorld/group)
        String grpPath = servPath + "/" + group;
        if (!ZooKeeperUtil.exists(zooKeeper, grpPath))
            ZooKeeperUtil.create(zooKeeper, grpPath, CreateMode.PERSISTENT);   // 持久节点
        // 创建本机节点(/rpc/providers/org.jasonf.HelloWorld/group/'公网/局域网ip':'端口号')
        String hostPath = grpPath + "/" + NetworkUtil.getIPAddr() + ":" + ProviderBootstrap.getInstance().getConfig().getPort();
        if (!ZooKeeperUtil.exists(zooKeeper, hostPath))
            ZooKeeperUtil.create(zooKeeper, hostPath, CreateMode.EPHEMERAL);    // 临时节点
        if (log.isDebugEnabled()) {
            log.debug("服务 [{}] 注册成功", iface);
        }
    }

    @Override
    public List<InetSocketAddress> detect(String iface) {
        // 拉取可提供服务的节点位置信息
        String group = InvokerBootstrap.getInstance().getConfig().getGroup();
        List<String> addresses = ZooKeeperUtil.getChildren(zooKeeper,
                Constant.PROVIDERS_ROOT_PATH + "/" + iface + "/" + group,
                ServiceMutation.getInstance());
        // 封装 ip 和 port
        List<InetSocketAddress> nodes = addresses.stream().map(address -> {
            String[] factor = address.split(":");
            return new InetSocketAddress(factor[0], Integer.parseInt(factor[1]));
        }).collect(Collectors.toList());
        if (nodes.isEmpty()) throw new ServiceNotFoundException("未找到任何可提供服务的节点");
        return nodes;
    }

    @Override
    public void disconnect() {
        ZooKeeperUtil.close(zooKeeper);
    }
}
