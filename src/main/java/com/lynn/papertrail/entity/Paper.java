package com.lynn.papertrail.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 论文实体类 - 支持多数据源
 * @author lynn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paper {

    /**
     * 论文唯一标识符
     */
    private String id;

    /**
     * 数据源类型 (ARXIV, IEEE, ACM, DBLP, etc.)
     */
    private PaperSource source;

    /**
     * 原始数据源ID
     */
    private String sourceId;

    /**
     * 论文标题
     */
    private String title;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 作者列表
     */
    private List<String> authors;

    /**
     * 发布日期
     */
    private LocalDateTime publishedDate;

    /**
     * 更新日期
     */
    private LocalDateTime updatedDate;

    /**
     * 分类标签
     */
    private List<String> categories;

    /**
     * 主要分类
     */
    private String primaryCategory;

    /**
     * PDF链接
     */
    private String pdfUrl;

    /**
     * 论文页面链接
     */
    private String paperUrl;

    /**
     * DOI标识符
     */
    private String doi;

    /**
     * 期刊/会议名称
     */
    private String venue;

    /**
     * 引用次数
     */
    private Integer citationCount;

    /**
     * 关键词
     */
    private List<String> keywords;

    /**
     * 扩展属性（用于不同数据源的特殊字段）
     */
    private Map<String, Object> extraProperties;

    /**
     * 论文数据源枚举
     */
    @Getter
    public enum PaperSource {
        ARXIV("arXiv"),
        IEEE("IEEE Xplore"),
        ACM("ACM Digital Library"),
        DBLP("DBLP"),
        PUBMED("PubMed"),
        SPRINGER("Springer"),
        ELSEVIER("Elsevier");

        private final String displayName;

        PaperSource(String displayName) {
            this.displayName = displayName;
        }

    }
}
