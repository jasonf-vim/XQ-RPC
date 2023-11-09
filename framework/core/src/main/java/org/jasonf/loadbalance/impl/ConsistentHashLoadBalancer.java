package org.jasonf.loadbalance.impl;

import org.jasonf.InvokerBootstrap;
import org.jasonf.loadbalance.AbstractLoadBalancer;
import org.jasonf.loadbalance.Selector;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @Author jasonf
 * @Date 2023/11/9
 * @Description 一致性哈希
 */

public class ConsistentHashLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected Selector getSelector(List<InetSocketAddress> addressList) {
        return new ConsistentHashSelector(addressList, 128);
    }

    private static class ConsistentHashSelector implements Selector {
        TreeMap<Integer, InetSocketAddress> circle = new TreeMap<>();   // 哈希环
        int virtualNodes;

        public ConsistentHashSelector(List<InetSocketAddress> addressList, int virtualNodes) {
            this.virtualNodes = virtualNodes;
            for (InetSocketAddress address : addressList) {
                addNode(address);
            }
        }

        private void addNode(InetSocketAddress address) {
            for (int i = 0; i < virtualNodes; i++) {
                int hash = hash(address + "-" + i);
                circle.put(hash, address);
            }
        }

        private void removeNode(InetSocketAddress address) {
            for (int i = 0; i < virtualNodes; i++) {
                int hash = hash(address + "-" + i);
                circle.remove(hash);
            }
        }

        private int hash(String source) {
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
            byte[] digest = md5.digest(source.getBytes());
            int end = digest.length - 1, hash = 0;
            // 取 md5 的后 4 个字节作为哈希值
            for (int i = 0; i < 4; i++) {
                hash <<= 8;
                hash |= digest[end - i];
            }
            return hash;
        }

        @Override
        public InetSocketAddress select() {
            if (circle.isEmpty()) throw new RuntimeException("服务列表为空");
            int hash = hash(InvokerBootstrap.MESSAGE_ID_THREAD_LOCAL.get().toString());
            InetSocketAddress address = circle.get(hash);
            if (address == null) {
                Map.Entry<Integer, InetSocketAddress> entry;
                address = (entry = circle.ceilingEntry(hash)) == null ? null : entry.getValue();
                if (address == null) address = circle.firstEntry().getValue();
            }
            return address;
        }

        @Override
        public void reBalance() {

        }
    }
}
