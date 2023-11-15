package org.jasonf;

import org.jasonf.annotation.Reference;
import org.jasonf.config.InvokeConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author jasonf
 * @Date 2023/11/15
 * @Description
 */

@Component
public class ProxyBeanPostProcessor implements BeanPostProcessor {
    private static final Map<Class<?>, Object> CACHE = new ConcurrentHashMap<>();
    private static final InvokeConfig CONFIG = new InvokeConfig();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (Field field : bean.getClass().getDeclaredFields()) {
            Reference reference = field.getAnnotation(Reference.class);
            if (reference == null) continue;
            Class<?> type = field.getType();
            Object obj = CACHE.get(type);   // 优先从缓存获取
            if (obj == null) {
                CONFIG.setInterface(type);
                obj = CONFIG.get();
                CACHE.put(type, obj);
            }   // 构造代理对象并及时缓存
            field.setAccessible(true);
            try {
                field.set(bean, obj);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return bean;
    }
}
