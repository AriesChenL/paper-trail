package com.lynn.papertrail.service.datasource;

import com.lynn.papertrail.dto.PaperSearchRequest;
import com.lynn.papertrail.dto.PaperSearchResponse;
import com.lynn.papertrail.entity.Paper;

/**
 * 论文数据源抽象接口
 * @author lynn
 */
public interface PaperDataSource {

    /**
     * 获取数据源类型
     */
    Paper.PaperSource getSourceType();

    /**
     * 搜索论文
     */
    PaperSearchResponse searchPapers(PaperSearchRequest request);

    /**
     * 根据ID获取论文详情
     */
    Paper getPaperById(String id);

    /**
     * 检查数据源是否可用
     */
    boolean isAvailable();

    /**
     * 获取数据源名称
     */
    String getSourceName();

    /**
     * 获取数据源优先级（数字越小优先级越高）
     */
    int getPriority();
}
