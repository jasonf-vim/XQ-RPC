package org.jasonf.boot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jasonf.Constant;
import org.jasonf.IDGenerator;
import org.jasonf.config.RegistryConfig;
import org.jasonf.loadbalance.AbstractLoadBalancer;
import org.jasonf.loadbalance.impl.ConsistentHashLoadBalancer;
import org.jasonf.registry.Registry;
import org.jasonf.transfer.enumeration.CompressorType;
import org.jasonf.transfer.enumeration.SerializeType;
import org.jasonf.util.XmlParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author jasonf
 * @Date 2023/11/12
 * @Description
 */

@Slf4j
@Data
public class Configuration {
    private String appName = "XQ";
    private int port = 8102;

    private Registry registry = new RegistryConfig("zookeeper://123.60.86.242:2181").getRegistry();

    private SerializeType serialize = SerializeType.JDK;
    private CompressorType compress = CompressorType.GZIP;

    private IDGenerator idGenerator = new IDGenerator(Constant.DATA_CENTER_ID, Constant.MACHINE_ID);
    private AbstractLoadBalancer loadBalancer = new ConsistentHashLoadBalancer(registry);

    public Configuration() {
        resolve();
    }

    private void resolve() {
        XmlParser xmlParser = new XmlParser("XQ-config.xml");
        String appName = resolveAppName(xmlParser);
        if (appName != null) this.appName = appName;
        int port = resolvePort(xmlParser);
        if (port >= 0) this.port = port;
        Registry registry = resolveRegistry(xmlParser);
        if (registry != null) this.registry = registry;
        SerializeType serialize = resolveSerialize(xmlParser);
        if (serialize != null) this.serialize = serialize;
        CompressorType compress = resolveCompress(xmlParser);
        if (compress != null) this.compress = compress;
        IDGenerator idGenerator = resolveIdGenerator(xmlParser);
        if (idGenerator != null) this.idGenerator = idGenerator;
        AbstractLoadBalancer loadBalancer = resolveLoadBalancer(xmlParser);
        if (loadBalancer != null) this.loadBalancer = loadBalancer;
    }

    private String resolveAppName(XmlParser xmlParser) {
        String appName = xmlParser.parse("/configuration/app/text()");
        if (appName == null || "".equals(appName)) {
            log.info("无法解析应用名, 将采用默认配置");
            return null;
        }
        return appName;
    }

    private int resolvePort(XmlParser xmlParser) {
        String port = xmlParser.parse("/configuration/port/text()");
        if (port == null || "".equals(port)) {
            log.info("无法解析端口号, 将采用默认配置");
            return -1;
        }
        return Integer.parseInt(port);
    }

    private Registry resolveRegistry(XmlParser xmlParser) {
        String url = xmlParser.parse("/configuration/registry/@url");
        if (url == null || "".equals(url)) {
            log.info("无法解析注册中心, 将采用默认配置");
            return null;
        }
        registry.disconnect();    // 关闭默认连接
        RegistryConfig config = new RegistryConfig(url);
        return config.getRegistry();
    }

    private SerializeType resolveSerialize(XmlParser xmlParser) {
        String type = xmlParser.parse("/configuration/serialize/@type");
        if (type == null || "".equals(type)) {
            log.info("无法解析序列化器, 将采用默认配置");
            return null;
        }
        SerializeType serialize = null;
        try {
            serialize = SerializeType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.info("序列化类型不合法, 将采用默认配置");
        }
        return serialize;
    }

    private CompressorType resolveCompress(XmlParser xmlParser) {
        String type = xmlParser.parse("/configuration/compress/@type");
        if (type == null || "".equals(type)) {
            log.info("无法解析压缩类型, 将采用默认配置");
            return null;
        }
        CompressorType compress = null;
        try {
            compress = CompressorType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.info("压缩器类型不合法, 将采用默认配置");
        }
        return compress;
    }

    private IDGenerator resolveIdGenerator(XmlParser xmlParser) {
        String className, room, server;
        if ((className = xmlParser.parse("/configuration/id-generator/@class")) == null || "".equals(className) ||
                (room = xmlParser.parse("/configuration/id-generator/@room")) == null || "".equals(room) ||
                (server = xmlParser.parse("/configuration/id-generator/@server")) == null || "".equals(server)) {
            log.info("无法解析ID生成器, 将采用默认配置");
            return null;
        }
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(long.class, long.class);
            long dataCenterId = Long.parseLong(room);
            long machineId = Long.parseLong(server);
            return (IDGenerator) constructor.newInstance(dataCenterId, machineId);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                IllegalAccessException | InvocationTargetException ex) {
            log.info("无法解析ID生成器, 将采用默认配置");
        }
        return null;
    }

    private AbstractLoadBalancer resolveLoadBalancer(XmlParser xmlParser) {
        String className = xmlParser.parse("/configuration/load-balancer/@class");
        if (className == null || "".equals(className)) {
            log.info("无法解析负载均衡器, 将采用默认配置");
            return null;
        }
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(Registry.class);
            return (AbstractLoadBalancer) constructor.newInstance(registry);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException ex) {
            log.info("无法解析负载均衡器, 将采用默认配置");
        }
        return null;
    }
}
