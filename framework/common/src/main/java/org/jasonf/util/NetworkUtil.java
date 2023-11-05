package org.jasonf.util;

import lombok.extern.slf4j.Slf4j;
import org.jasonf.exception.NetworkException;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

@Slf4j
public class NetworkUtil {
    public static String getIPAddr() {
        try {
            // 获取本机所有网卡信息
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // 滤除 未启用网卡 | 虚拟环回网卡 | 虚拟网卡
                if (!iface.isUp() || iface.isLoopback() || iface.isVirtual()) continue;
                Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    // 滤除 ipv6地址 | 环回地址
                    if (inetAddress instanceof Inet6Address || inetAddress.isLoopbackAddress()) continue;
                    String ipAddr = inetAddress.getHostAddress();
                    log.info("局域网ip地址: [{}]", ipAddr);
                    return ipAddr;
                }
            }
            throw new NetworkException();
        } catch (SocketException ex) {
            log.error("获取局域网ip时发生异常: ", ex);
            throw new NetworkException(ex);
        }
    }
}
