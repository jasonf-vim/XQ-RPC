package org.jasonf.boot;

import lombok.extern.slf4j.Slf4j;
import org.jasonf.config.RegistryConfig;
import org.jasonf.generator.IDGenerator;
import org.jasonf.loadbalance.AbstractLoadBalancer;
import org.jasonf.registry.Registry;
import org.jasonf.transfer.enumeration.CompressorType;
import org.jasonf.transfer.enumeration.SerializeType;
import org.jasonf.util.XmlParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author jasonf
 * @Date 2023/11/13
 * @Description
 */

@Slf4j
public class XmlResolver {
    public static void resolve(Configuration config) {
        XmlParser xmlParser = new XmlParser("XQ-config.xml");
        String appName = resolveAppName(xmlParser);
        if (appName != null) config.setAppName(appName);
        String group = resolveGroup(xmlParser);
        if (group != null) config.setGroup(group);
        int port = resolvePort(xmlParser);
        if (port >= 0) config.setPort(port);
        Registry registry = resolveRegistry(xmlParser, config.getRegistry());
        if (registry != null) config.setRegistry(registry);
        SerializeType serialize = resolveSerialize(xmlParser);
        if (serialize != null) config.setSerialize(serialize);
        CompressorType compress = resolveCompress(xmlParser);
        if (compress != null) config.setCompress(compress);
        IDGenerator idGenerator = resolveIdGenerator(xmlParser);
        if (idGenerator != null) config.setIdGenerator(idGenerator);
        AbstractLoadBalancer loadBalancer = resolveLoadBalancer(xmlParser, config.getRegistry());
        if (loadBalancer != null) config.setLoadBalancer(loadBalancer);
    }

    private static String resolveAppName(XmlParser xmlParser) {
        String appName = xmlParser.parse("/configuration/app/text()");
        if (appName == null || "".equals(appName)) {
            log.info("无法解析应用名, 将采用默认配置");
            return null;
        }
        return appName;
    }

    private static String resolveGroup(XmlParser xmlParser) {
        String group = xmlParser.parse("/configuration/group/text()");
        if (group == null || "".equals(group)) {
            log.info("无法解析分组名, 将采用默认配置");
            return null;
        }
        return group;
    }

    private static int resolvePort(XmlParser xmlParser) {
        String port = xmlParser.parse("/configuration/port/text()");
        if (port == null || "".equals(port)) {
            log.info("无法解析端口号, 将采用默认配置");
            return -1;
        }
        return Integer.parseInt(port);
    }

    private static Registry resolveRegistry(XmlParser xmlParser, Registry registry) {
        String url = xmlParser.parse("/configuration/registry/@url");
        if (url == null || "".equals(url)) {
            log.info("无法解析注册中心, 将采用默认配置");
            return null;
        }
        registry.disconnect();    // 关闭默认连接
        RegistryConfig config = new RegistryConfig(url);
        return config.getRegistry();
    }

    private static SerializeType resolveSerialize(XmlParser xmlParser) {
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

    private static CompressorType resolveCompress(XmlParser xmlParser) {
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

    private static IDGenerator resolveIdGenerator(XmlParser xmlParser) {
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

    private static AbstractLoadBalancer resolveLoadBalancer(XmlParser xmlParser, Registry registry) {
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
