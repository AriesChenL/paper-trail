package com.lynn.papertrail.dto;

import com.lynn.papertrail.entity.Paper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * 论文搜索请求DTO - 支持多数据源
 * @author lynn
 */
@Data
public class PaperSearchRequest {

    /**
     * 搜索关键词
     */
    @NotBlank(message = "搜索关键词不能为空")
    private String query;

    /**
     * 起始位置，默认为0
     */
    @Min(value = 0, message = "起始位置不能小于0")
    private int start = 0;

    /**
     * 返回结果数量，默认为10，最大100
     */
    @Min(value = 1, message = "返回数量不能小于1")
    @Max(value = 100, message = "返回数量不能大于100")
    private int maxResults = 10;

    /**
     * 排序方式：relevance(相关性), lastUpdatedDate(最后更新时间), submittedDate(提交时间)
     */
    private String sortBy = "relevance";

    /**
     * 排序顺序：ascending(升序), descending(降序)
     */
    private String sortOrder = "descending";

    /**
     * 搜索字段：all(全部), title(标题), author(作者), abstract(摘要), comment(评论),
     * journal(期刊), subject(主题), report(报告), id(ID)
     */
    private String searchField = "all";

    /**
     * 指定搜索的数据源，如果为空则搜索所有可用数据源
     */
    private Set<Paper.PaperSource> dataSources;

    /**
     * 搜索日期范围 - 开始日期
     */
    private LocalDate dateFrom;

    /**
     * 搜索日期范围 - 结束日期
     */
    private LocalDate dateTo;

    /**
     * 分类过滤器
     */
    private Set<String> categories;

    /**
     * 作者过滤器
     */
    private Set<String> authorFilter;

    /**
     * 是否启用去重（默认true）
     */
    private boolean enableDeduplication = true;

    /**
     * 是否启用并行搜索（默认true）
     */
    private boolean enableParallelSearch = true;
}
