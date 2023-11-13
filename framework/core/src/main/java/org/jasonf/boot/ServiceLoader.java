package org.jasonf.boot;

import lombok.extern.slf4j.Slf4j;
import org.jasonf.generator.IDGenerator;
import org.jasonf.registry.Registry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author jasonf
 * @Date 2023/11/13
 * @Description
 */

@Slf4j
public class ServiceLoader {
    private static final String PREFIX = "META-INF/services";

    private static final Map<String, String> CONFIG = new ConcurrentHashMap<>(8);
    private static final Map<Class<?>, Object> IMPL_CACHE = new ConcurrentHashMap<>(8);

    static {
        // 缓存 SPI 配置信息
        URL url = Thread.currentThread().getContextClassLoader().getResource(PREFIX);
        if (url == null) {
            log.info("目录 classpath:/{} 不存在", PREFIX);
        } else {
            File dir = new File(url.getPath());
            File[] files = dir.listFiles();
            if (files == null) {
                log.info("目录 classpath:/{} 下未发现可用服务", PREFIX);
            } else {
                for (File file : files) {
                    String impl = read(file);
                    if (impl != null) CONFIG.put(file.getName(), impl);
                }
            }
        }
    }

    private static String read(File file) {
        try (FileReader reader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String impl = bufferedReader.readLine();
            if (impl == null) throw new RuntimeException();
            return impl;
        } catch (IOException | RuntimeException ex) {
            log.info("文件 [{}] 无法读取或内容为空", file.getPath());
        }
        return null;
    }

    private static void makeCache(Class<?> clazz) {
        String iface = clazz.getName();
        String impl = CONFIG.get(iface);
        if (impl == null) {
            log.info("暂未配置 [{}] 的实现", iface);
            return;
        }
        try {
            Class<?> implClazz = Class.forName(impl);
            Object obj = implClazz.newInstance();
            IMPL_CACHE.put(clazz, obj);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            log.info("无法将 [{}] 实例化", impl);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T load(Class<T> clazz) {
        if (!IMPL_CACHE.containsKey(clazz)) makeCache(clazz);
        return (T) IMPL_CACHE.get(clazz);
    }

    public static void load(Configuration config) {
        Registry registry = load(Registry.class);
        if (registry != null) {
            config.getRegistry().disconnect();    // 关闭默认连接
            config.setRegistry(registry);
        }
        IDGenerator idGenerator = load(IDGenerator.class);
        if (idGenerator != null) config.setIdGenerator(idGenerator);
    }
}
