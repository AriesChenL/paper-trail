package com.lynn.papertrail.dto;

import com.lynn.papertrail.entity.Paper;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 论文搜索响应DTO - 支持多数据源
 * @author lynn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaperSearchResponse {

    /**
     * 论文列表
     */
    private List<Paper> papers;

    /**
     * 总结果数
     */
    private int totalResults;

    /**
     * 起始位置
     */
    private int startIndex;

    /**
     * 每页数量
     */
    private int itemsPerPage;

    /**
     * 查询关键词
     */
    private String query;

    /**
     * 数据源统计信息
     */
    private Map<String, Integer> sourceStatistics;

    /**
     * 搜索使用的数据源
     */
    private Set<Paper.PaperSource> searchedSources;

    /**
     * 搜索耗时（毫秒）
     */
    private long searchTimeMs;

    /**
     * 是否进行了去重处理
     */
    private boolean deduplicationApplied;

    // 为了兼容现有代码，保留原有构造函数
    public PaperSearchResponse(List<Paper> papers, int totalResults, int startIndex, int itemsPerPage, String query) {
        this.papers = papers;
        this.totalResults = totalResults;
        this.startIndex = startIndex;
        this.itemsPerPage = itemsPerPage;
        this.query = query;
    }
}
