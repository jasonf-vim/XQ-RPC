package org.jasonf;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.jasonf.util.ZooKeeperUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @Author jasonf
 * @Date 2023/11/5
 * @Description
 */

public class ManageApp {
    public static void main(String[] args) {
        // 建立连接, 获取 zooKeeper 实例
        ZooKeeper zooKeeper = ZooKeeperUtil.getZooKeeper();

        // 建立持久节点
        List<String> paths = Arrays.asList(
                Constant.RPC_ROOT_PATH,
                Constant.PROVIDERS_ROOT_PATH,
                Constant.INVOKERS_ROOT_PATH
        );
        paths.forEach(path -> {
            if (!ZooKeeperUtil.exists(zooKeeper, path))
                ZooKeeperUtil.create(zooKeeper, path, CreateMode.PERSISTENT);
        });

        // 关闭 ZooKeeper 连接
        ZooKeeperUtil.close(zooKeeper);
    }
}
