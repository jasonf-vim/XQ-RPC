package org.jasonf.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.jasonf.Constant;
import org.jasonf.exception.ZooKeeperException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

@Slf4j
public class ZooKeeperUtil {
    /**
     * 判断节点是否存在
     *
     * @param zooKeeper zooKeeper 实例
     * @param path      节点路径
     * @return true 已存在, false 不存在
     */
    public static boolean exists(ZooKeeper zooKeeper, String path) {
        return exists(zooKeeper, path, null);
    }

    public static boolean exists(ZooKeeper zooKeeper, String path, Watcher watcher) {
        try {
            return zooKeeper.exists(path, watcher) != null;
        } catch (KeeperException | InterruptedException ex) {
            log.error("判断 [{}] 节点是否存在时发生异常: ", path, ex);
            throw new ZooKeeperException(ex);
        }
    }

    /**
     * 创建节点
     *
     * @param zooKeeper  zooKeeper 实例
     * @param path       节点路径
     * @param createMode 创建模式
     * @return 节点的最终路径
     */
    public static String create(ZooKeeper zooKeeper, String path, CreateMode createMode) {
        return create(zooKeeper, path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
    }

    public static String create(ZooKeeper zooKeeper, String path, byte[] data, List<ACL> license, CreateMode createMode) {
        try {
            String actualPath = zooKeeper.create(path, data, license, createMode);
            log.info("节点 [{}] 创建成功", actualPath);
            return actualPath;
        } catch (KeeperException | InterruptedException ex) {
            log.error("创建 [{}] 节点时发生异常: ", path, ex);
            throw new ZooKeeperException(ex);
        }
    }

    public static List<String> getChildren(ZooKeeper zooKeeper, String path) {
        return getChildren(zooKeeper, path, null);
    }

    public static List<String> getChildren(ZooKeeper zooKeeper, String path, Watcher watcher) {
        try {
            return zooKeeper.getChildren(path, watcher);
        } catch (KeeperException | InterruptedException ex) {
            log.error("获取 [{}] 节点的子节点时发生异常", path, ex);
            throw new ZooKeeperException(ex);
        }
    }

    public static ZooKeeper getZooKeeper() {
        return getZooKeeper(Constant.DEFAULT_ZK_CONNECT, Constant.DEFAULT_TIMEOUT);
    }

    /**
     * 连接 ZooKeeper 并获取实例
     *
     * @param connect 连接串
     * @param timeout 超时时间
     * @return zooKeeper 实例
     */
    public static ZooKeeper getZooKeeper(String connect, int timeout) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            ZooKeeper zooKeeper = new ZooKeeper(connect, timeout, event -> {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    log.info("ZooKeeper 连接成功");
                    countDownLatch.countDown();
                }
            });    // 建立连接
            countDownLatch.await();
            return zooKeeper;
        } catch (IOException | InterruptedException ex) {
            log.error("创建 ZooKeeper 实例时发生异常: ", ex);
            throw new ZooKeeperException();
        }
    }

    public static void close(ZooKeeper zooKeeper) {
        try {
            zooKeeper.close();
        } catch (InterruptedException ex) {
            log.error("关闭 ZooKeeper 连接时发生异常: ", ex);
        }
    }
}
