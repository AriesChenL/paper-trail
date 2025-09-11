package com.lynn.papertrail.service.datasource;

import com.lynn.papertrail.dto.PaperSearchRequest;
import com.lynn.papertrail.dto.PaperSearchResponse;
import com.lynn.papertrail.entity.Paper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 论文数据源抽象基类
 * @author lynn
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPaperDataSource implements PaperDataSource {

    protected final WebClient webClient;

    @Override
    public PaperSearchResponse searchPapers(PaperSearchRequest request) {
        try {
            log.info("开始从 {} 搜索论文，关键词：{}", getSourceName(), request.getQuery());

            PaperSearchResponse response = doSearchPapers(request);

            log.info("从 {} 成功获取 {} 篇论文", getSourceName(), response.getPapers().size());
            return response;

        } catch (Exception e) {
            log.error("从 {} 搜索论文时发生错误", getSourceName(), e);
            throw new RuntimeException("搜索论文失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Paper getPaperById(String id) {
        try {
            log.info("从 {} 获取论文详情，ID：{}", getSourceName(), id);
            return doGetPaperById(id);
        } catch (Exception e) {
            log.error("从 {} 获取论文详情时发生错误，ID：{}", getSourceName(), id, e);
            return null;
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            return doHealthCheck();
        } catch (Exception e) {
            log.warn("{} 数据源不可用: {}", getSourceName(), e.getMessage());
            return false;
        }
    }

    /**
     * 具体的搜索实现
     */
    protected abstract PaperSearchResponse doSearchPapers(PaperSearchRequest request);

    /**
     * 具体的获取论文详情实现
     */
    protected abstract Paper doGetPaperById(String id);

    /**
     * 健康检查实现
     */
    protected abstract boolean doHealthCheck();

    /**
     * 构建通用的Paper ID（格式：数据源_原始ID）
     */
    protected String buildPaperId(String sourceId) {
        return getSourceType().name().toLowerCase() + "_" + sourceId;
    }
}
