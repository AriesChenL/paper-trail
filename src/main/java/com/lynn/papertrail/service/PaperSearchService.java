package com.lynn.papertrail.service;

import com.lynn.papertrail.dto.PaperSearchRequest;
import com.lynn.papertrail.dto.PaperSearchResponse;
import com.lynn.papertrail.entity.Paper;
import com.lynn.papertrail.service.datasource.PaperDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 统一的论文搜索服务 - 聚合多个数据源
 * @author lynn
 */
@Service
@Slf4j
public class PaperSearchService {

    private final List<PaperDataSource> dataSources;
    private final Map<Paper.PaperSource, PaperDataSource> dataSourceMap;

    public PaperSearchService(List<PaperDataSource> dataSources) {
        this.dataSources = dataSources.stream()
                .sorted(Comparator.comparingInt(PaperDataSource::getPriority))
                .collect(Collectors.toList());

        this.dataSourceMap = dataSources.stream()
                .collect(Collectors.toMap(
                    PaperDataSource::getSourceType,
                    source -> source,
                    (existing, replacement) -> existing
                ));

        log.info("初始化论文搜索服务，支持的数据源: {}",
                dataSources.stream()
                    .map(PaperDataSource::getSourceName)
                    .collect(Collectors.joining(", ")));
    }

    /**
     * 搜索论文 - 支持多数据源
     */
    public PaperSearchResponse searchPapers(PaperSearchRequest request) {
        // 如果指定了特定数据源
        if (request.getDataSources() != null && !request.getDataSources().isEmpty()) {
            return searchFromSpecificSources(request, request.getDataSources());
        }

        // 默认使用所有可用数据源
        Set<Paper.PaperSource> availableSources = getAvailableDataSources();
        return searchFromSpecificSources(request, availableSources);
    }

    /**
     * 从指定数据源搜索
     */
    private PaperSearchResponse searchFromSpecificSources(PaperSearchRequest request, Set<Paper.PaperSource> sources) {
        if (sources.size() == 1) {
            // 单个数据源，直接调用
            Paper.PaperSource source = sources.iterator().next();
            PaperDataSource dataSource = dataSourceMap.get(source);
            if (dataSource != null && dataSource.isAvailable()) {
                return dataSource.searchPapers(request);
            }
            return createEmptyResponse(request);
        }

        // 多个数据源，并行调用
        return searchFromMultipleSources(request, sources);
    }

    /**
     * 并行搜索多个数据源
     */
    private PaperSearchResponse searchFromMultipleSources(PaperSearchRequest request, Set<Paper.PaperSource> sources) {
        List<CompletableFuture<PaperSearchResponse>> futures = sources.stream()
                .filter(source -> dataSourceMap.containsKey(source))
                .map(source -> dataSourceMap.get(source))
                .filter(PaperDataSource::isAvailable)
                .map(dataSource -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return dataSource.searchPapers(request);
                    } catch (Exception e) {
                        log.error("从 {} 搜索时发生错误", dataSource.getSourceName(), e);
                        return createEmptyResponse(request);
                    }
                }))
                .collect(Collectors.toList());

        try {
            // 等待所有搜索完成（最多30秒）
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0]));

            allFutures.get(30, TimeUnit.SECONDS);

            // 合并结果
            return mergeSearchResults(futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()), request);

        } catch (Exception e) {
            log.error("并行搜索时发生错误", e);
            // 返回已完成的结果
            return mergeSearchResults(futures.stream()
                    .filter(CompletableFuture::isDone)
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()), request);
        }
    }

    /**
     * 合并多个数据源的搜索结果
     */
    private PaperSearchResponse mergeSearchResults(List<PaperSearchResponse> responses, PaperSearchRequest request) {
        List<Paper> allPapers = responses.stream()
                .flatMap(response -> response.getPapers().stream())
                .collect(Collectors.toList());

        // 去重（基于标题和作者的相似性）
        List<Paper> deduplicatedPapers = deduplicatePapers(allPapers);

        // 排序（按数据源优先级和相关性）
        List<Paper> sortedPapers = sortPapers(deduplicatedPapers, request);

        // 分页处理
        List<Paper> pagedPapers = paginatePapers(sortedPapers, request);

        int totalResults = responses.stream()
                .mapToInt(PaperSearchResponse::getTotalResults)
                .sum();

        return new PaperSearchResponse(
                pagedPapers,
                totalResults,
                request.getStart(),
                pagedPapers.size(),
                request.getQuery()
        );
    }

    /**
     * 根据ID获取论文详情
     */
    public Paper getPaperById(String id) {
        // 从ID中解析数据源类型
        Paper.PaperSource sourceType = parseSourceFromId(id);

        if (sourceType != null && dataSourceMap.containsKey(sourceType)) {
            PaperDataSource dataSource = dataSourceMap.get(sourceType);
            if (dataSource.isAvailable()) {
                return dataSource.getPaperById(id);
            }
        }

        // 如果无法确定数据源，尝试所有数据源
        for (PaperDataSource dataSource : dataSources) {
            if (dataSource.isAvailable()) {
                try {
                    Paper paper = dataSource.getPaperById(id);
                    if (paper != null) {
                        return paper;
                    }
                } catch (Exception e) {
                    log.debug("从 {} 获取论文详情失败", dataSource.getSourceName(), e);
                }
            }
        }

        return null;
    }

    /**
     * 获取可用的数据源列表
     */
    public Set<Paper.PaperSource> getAvailableDataSources() {
        return dataSources.stream()
                .filter(PaperDataSource::isAvailable)
                .map(PaperDataSource::getSourceType)
                .collect(Collectors.toSet());
    }

    /**
     * 获取数据源健康状态
     */
    public Map<String, Boolean> getDataSourceHealth() {
        return dataSources.stream()
                .collect(Collectors.toMap(
                    PaperDataSource::getSourceName,
                    PaperDataSource::isAvailable
                ));
    }

    private Paper.PaperSource parseSourceFromId(String id) {
        if (id == null || !id.contains("_")) {
            return null;
        }

        String sourcePrefix = id.split("_")[0].toUpperCase();
        try {
            return Paper.PaperSource.valueOf(sourcePrefix);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private List<Paper> deduplicatePapers(List<Paper> papers) {
        // 简单的去重逻辑：基于标题的相似性
        Map<String, Paper> uniquePapers = new LinkedHashMap<>();

        for (Paper paper : papers) {
            String normalizedTitle = normalizeTitle(paper.getTitle());
            if (!uniquePapers.containsKey(normalizedTitle)) {
                uniquePapers.put(normalizedTitle, paper);
            } else {
                // 如果已存在相似标题的论文，选择优先级更高的数据源
                Paper existing = uniquePapers.get(normalizedTitle);
                if (getSourcePriority(paper.getSource()) < getSourcePriority(existing.getSource())) {
                    uniquePapers.put(normalizedTitle, paper);
                }
            }
        }

        return new ArrayList<>(uniquePapers.values());
    }

    private String normalizeTitle(String title) {
        if (title == null) return "";
        return title.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private int getSourcePriority(Paper.PaperSource source) {
        return dataSourceMap.get(source) != null ?
                dataSourceMap.get(source).getPriority() : Integer.MAX_VALUE;
    }

    private List<Paper> sortPapers(List<Paper> papers, PaperSearchRequest request) {
        // 按数据源优先级和发布时间排序
        return papers.stream()
                .sorted(Comparator
                    .comparingInt((Paper p) -> getSourcePriority(p.getSource()))
                    .thenComparing((Paper p) -> p.getPublishedDate() != null ? p.getPublishedDate() :
                        java.time.LocalDateTime.MIN, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    private List<Paper> paginatePapers(List<Paper> papers, PaperSearchRequest request) {
        int start = Math.max(0, request.getStart());
        int maxResults = Math.max(1, Math.min(100, request.getMaxResults()));
        int end = Math.min(papers.size(), start + maxResults);

        if (start >= papers.size()) {
            return Collections.emptyList();
        }

        return papers.subList(start, end);
    }

    private PaperSearchResponse createEmptyResponse(PaperSearchRequest request) {
        return new PaperSearchResponse(Collections.emptyList(), 0, request.getStart(), 0, request.getQuery());
    }
}
