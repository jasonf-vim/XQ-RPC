package org.jasonf.watcher;

import io.netty.channel.Channel;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.jasonf.InvokerBootstrap;
import org.jasonf.registry.Registry;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * @Author jasonf
 * @Date 2023/11/10
 * @Description
 */

public class ServiceMutation implements Watcher {
    private static volatile ServiceMutation serviceMutation;
    private Registry registry = InvokerBootstrap.getInstance().getConfig().getRegistry();

    private ServiceMutation() {
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeChildrenChanged) {
            // 重新拉取服务列表
            String iface = event.getPath().substring(event.getPath().lastIndexOf("/") + 1);
            List<InetSocketAddress> serviceList = registry.detect(iface);   // 重新添加 watcher

            // 辅助心跳检测感知服务下线
            for (Map.Entry<InetSocketAddress, Channel> entry : InvokerBootstrap.CHANNEL_CACHE.entrySet()) {
                InetSocketAddress address = entry.getKey();
                if (!serviceList.contains(address)) {
                    InvokerBootstrap.CHANNEL_CACHE.remove(address);
                    InvokerBootstrap.RESPONSE_TIME.remove(address);
                }
            }

            // 配合负载均衡感知服务上下线
            InvokerBootstrap.getInstance().getConfig().getLoadBalancer().reLoadBalance(iface, serviceList);
        }
    }

    public static ServiceMutation getInstance() {
        if (serviceMutation == null) {
            synchronized (ServiceMutation.class) {
                if (serviceMutation == null) {
                    serviceMutation = new ServiceMutation();
                }
            }
        }
        return serviceMutation;
    }
}
