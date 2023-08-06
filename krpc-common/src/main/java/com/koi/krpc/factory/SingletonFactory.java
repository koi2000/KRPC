package com.koi.krpc.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author koi
 * @date 2023/8/6 16:41
 */
// 单例工厂
public class SingletonFactory {
    private static Map<Class, Object> objectMap = new HashMap<>();

    public SingletonFactory() {
    }

    public static <T> T getInstance(Class<T> clazz) {
        Object instance = objectMap.get(clazz);
        synchronized (clazz) {
            if (instance == null) {
                try {
                    instance = clazz.newInstance();
                    objectMap.put(clazz, instance);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return clazz.cast(instance);
    }
}
