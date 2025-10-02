package com.lynn.papertrail.util;

/**
 * 演示UserContext和UserContextHolder用法的示例类
 */
public class UserContextDemo {

    public static void main(String[] args) {
        System.out.println("=== UserContext 演示 ===");
        
        // 设置一些用户信息
        UserContextHolder.setUserId(123L);
        UserContextHolder.setUsername("john_doe");
        UserContextHolder.setUserRole("ADMIN");
        UserContextHolder.setUserEmail("john@example.com");
        
        // 设置自定义属性
        UserContextHolder.setAttribute("department", "Engineering");
        UserContextHolder.setAttribute("sessionTimeout", 30);
        
        // 检索并显示用户信息
        System.out.println("用户ID: " + UserContextHolder.getUserId());
        System.out.println("用户名: " + UserContextHolder.getUsername());
        System.out.println("用户角色: " + UserContextHolder.getUserRole());
        System.out.println("用户邮箱: " + UserContextHolder.getUserEmail());
        System.out.println("部门: " + UserContextHolder.getAttribute("department"));
        System.out.println("会话超时: " + UserContextHolder.getAttribute("sessionTimeout"));
        
        // 演示默认值的使用
        String nonExistent = UserContextHolder.getAttribute("nonExistent", "default_value");
        System.out.println("不存在的属性使用默认值: " + nonExistent);
        
        // 显示所有上下文信息
        System.out.println("\n完整上下文映射: " + UserContextHolder.getContext());
        
        // 清除上下文
        UserContextHolder.clear();
        
        // 检查清除后的值
        System.out.println("\n清除上下文后:");
        System.out.println("用户ID: " + UserContextHolder.getUserId());
        System.out.println("用户名: " + UserContextHolder.getUsername());
        System.out.println("上下文映射大小: " + UserContextHolder.getContext().size());
        
        // 测试UserContext（通用目的）
        System.out.println("\n=== UserContext（通用目的）演示 ===");
        UserContext.set("key1", "value1");
        UserContext.set("number", 42);
        UserContext.set("isActive", true);
        
        System.out.println("key1: " + UserContext.get("key1"));
        System.out.println("number: " + UserContext.get("number"));
        System.out.println("isActive: " + UserContext.get("isActive"));
        
        // 清理
        UserContext.clear();
    }
}