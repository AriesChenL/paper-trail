package com.lynn.papertrail.util;

import org.springframework.stereotype.Component;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import jakarta.servlet.*;
import java.io.IOException;

/**
 * Servlet过滤器，确保在每个请求后正确清理UserContext。
 * 通过在每个请求后清除ThreadLocal变量来防止内存泄漏。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserContextCleanupFilter implements Filter {

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, jakarta.servlet.FilterChain chain)
            throws IOException, jakarta.servlet.ServletException {
        
        try {
            chain.doFilter(request, response);
        } finally {
            // 请求处理完成后始终清理UserContext
            UserContextHolder.clear();
        }
    }
}