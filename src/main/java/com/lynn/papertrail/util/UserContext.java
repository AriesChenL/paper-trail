package com.lynn.papertrail.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于ThreadLocal的上下文管理类，用于临时存储用户信息。
 * 该类提供了一种在线程安全的方式下存储和检索用户相关数据的方法。
 *
 * @author lynn
 */
public class UserContext {

    private static final ThreadLocal<Map<String, Object>> USER_CONTEXT = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new ConcurrentHashMap<>();
        }
    };

    /**
     * 在当前线程的上下文中设置一个值
     *
     * @param key   上下文值的键
     * @param value 要存储的值
     */
    public static void set(String key, Object value) {
        USER_CONTEXT.get().put(key, value);
    }

    /**
     * 从当前线程的上下文中获取一个值
     *
     * @param key 上下文值的键
     * @return 使用给定键存储的值，如果未找到则返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        Map<String, Object> context = USER_CONTEXT.get();
        return (T) context.get(key);
    }

    /**
     * 从当前线程的上下文中获取一个值，如果未找到则返回默认值
     *
     * @param key          上下文值的键
     * @param defaultValue 如果未找到键则返回的默认值
     * @return 使用给定键存储的值，如果未找到则返回默认值
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, T defaultValue) {
        Map<String, Object> context = USER_CONTEXT.get();
        T value = (T) context.get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 从当前线程的上下文中移除一个值
     *
     * @param key 要移除的上下文值的键
     */
    public static void remove(String key) {
        USER_CONTEXT.get().remove(key);
    }

    /**
     * 清除当前线程上下文中的所有值
     */
    public static void clear() {
        USER_CONTEXT.remove();
    }

    /**
     * 获取当前线程的整个上下文映射
     *
     * @return 上下文映射
     */
    public static Map<String, Object> getContext() {
        return USER_CONTEXT.get();
    }

    /**
     * 检查当前线程的上下文中是否存在指定键
     *
     * @param key 要检查的键
     * @return 如果上下文中存在该键则返回true，否则返回false
     */
    public static boolean containsKey(String key) {
        return USER_CONTEXT.get().containsKey(key);
    }
}