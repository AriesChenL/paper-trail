package com.lynn.papertrail.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于ThreadLocal的用户信息上下文管理器。
 * 该类提供便捷的方法来存储和检索用户特定的数据。
 *
 * @author lynn
 */
public class UserContextHolder {

    private static final ThreadLocal<Map<String, Object>> USER_CONTEXT = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new ConcurrentHashMap<>();
        }
    };

    // 常用用户相关的键
    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    public static final String USER_ROLE = "userRole";
    public static final String USER_EMAIL = "userEmail";

    /**
     * 在当前线程的上下文中设置用户ID
     *
     * @param userId 要存储的用户ID
     */
    public static void setUserId(Long userId) {
        USER_CONTEXT.get().put(USER_ID, userId);
    }

    /**
     * 从当前线程的上下文中获取用户ID
     *
     * @return 用户ID，如果未找到则返回null
     */
    public static Long getUserId() {
        return (Long) USER_CONTEXT.get().get(USER_ID);
    }

    /**
     * 在当前线程的上下文中设置用户名
     *
     * @param username 要存储的用户名
     */
    public static void setUsername(String username) {
        USER_CONTEXT.get().put(USERNAME, username);
    }

    /**
     * 从当前线程的上下文中获取用户名
     *
     * @return 用户名，如果未找到则返回null
     */
    public static String getUsername() {
        return (String) USER_CONTEXT.get().get(USERNAME);
    }

    /**
     * 在当前线程的上下文中设置用户角色
     *
     * @param userRole 要存储的用户角色
     */
    public static void setUserRole(String userRole) {
        USER_CONTEXT.get().put(USER_ROLE, userRole);
    }

    /**
     * 从当前线程的上下文中获取用户角色
     *
     * @return 用户角色，如果未找到则返回null
     */
    public static String getUserRole() {
        return (String) USER_CONTEXT.get().get(USER_ROLE);
    }

    /**
     * 在当前线程的上下文中设置用户邮箱
     *
     * @param userEmail 要存储的用户邮箱
     */
    public static void setUserEmail(String userEmail) {
        USER_CONTEXT.get().put(USER_EMAIL, userEmail);
    }

    /**
     * 从当前线程的上下文中获取用户邮箱
     *
     * @return 用户邮箱，如果未找到则返回null
     */
    public static String getUserEmail() {
        return (String) USER_CONTEXT.get().get(USER_EMAIL);
    }

    /**
     * 在当前线程的上下文中设置一个自定义属性
     *
     * @param key   属性的键
     * @param value 要存储的值
     */
    public static void setAttribute(String key, Object value) {
        USER_CONTEXT.get().put(key, value);
    }

    /**
     * 从当前线程的上下文中获取一个自定义属性
     *
     * @param key 属性的键
     * @return 使用给定键存储的值，如果未找到则返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(String key) {
        return (T) USER_CONTEXT.get().get(key);
    }

    /**
     * 从当前线程的上下文中获取一个自定义属性，如果未找到则返回默认值
     *
     * @param key          属性的键
     * @param defaultValue 如果未找到键则返回的默认值
     * @return 使用给定键存储的值，如果未找到则返回默认值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(String key, T defaultValue) {
        T value = (T) USER_CONTEXT.get().get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 从当前线程的上下文中移除一个自定义属性
     *
     * @param key 要移除的属性的键
     */
    public static void removeAttribute(String key) {
        USER_CONTEXT.get().remove(key);
    }

    /**
     * 清除当前线程的所有用户上下文数据
     */
    public static void clear() {
        USER_CONTEXT.remove();
    }

    /**
     * 获取当前线程的整个用户上下文映射
     *
     * @return 用户上下文映射
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