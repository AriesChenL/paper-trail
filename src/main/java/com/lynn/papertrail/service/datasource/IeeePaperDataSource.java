package com.lynn.papertrail.service.datasource;

import com.lynn.papertrail.dto.PaperSearchRequest;
import com.lynn.papertrail.dto.PaperSearchResponse;
import com.lynn.papertrail.entity.Paper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

/**
 * IEEE Xplore 论文数据源实现 (示例)
 * 这是一个示例实现，展示如何扩展新的数据源
 * @author lynn
 */
@Slf4j
@Service
public class IeeePaperDataSource extends AbstractPaperDataSource {

    // IEEE Xplore API 通常需要 API Key，这里作为示例
    private static final String IEEE_API_BASE_URL = "https://ieeexploreapi.ieee.org/api/v1/search/articles";

    public IeeePaperDataSource(WebClient webClient) {
        super(webClient);
    }

    @Override
    public Paper.PaperSource getSourceType() {
        return Paper.PaperSource.IEEE;
    }

    @Override
    public String getSourceName() {
        return "IEEE Xplore";
    }

    @Override
    public int getPriority() {
        return 2; // IEEE 作为第二优先级数据源
    }

    @Override
    protected PaperSearchResponse doSearchPapers(PaperSearchRequest request) {
        // 注意：这是一个示例实现
        // 实际的 IEEE Xplore API 需要 API Key 和复杂的认证流程
        log.info("IEEE Xplore 数据源当前为示例实现，暂不支持实际搜索");

        // 返回空结果，避免实际调用
        return new PaperSearchResponse(Collections.emptyList(), 0, request.getStart(), 0, request.getQuery());
    }

    @Override
    protected Paper doGetPaperById(String id) {
        log.info("IEEE Xplore 数据源当前为示例实现，暂不支持根据ID获取论文");
        return null;
    }

    @Override
    protected boolean doHealthCheck() {
        // 示例实现总是返回不可用状态
        log.debug("IEEE Xplore 数据源为示例实现，标记为不可用");
        return false;
    }
}
