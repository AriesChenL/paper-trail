package com.lynn.papertrail.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付相关工具类
 * @author lynn
 */
public class PaymentUtils {
    
    /**
     * 解析查询字符串为Map
     */
    public static Map<String, String> parseQueryString(String queryString) {
        // 简单解析查询字符串
        Map<String, String> params = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    String key = pair.substring(0, idx);
                    String value = pair.substring(idx + 1);
                    params.put(key, value);
                }
            }
        }
        return params;
    }
}