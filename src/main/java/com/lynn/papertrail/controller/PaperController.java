package com.lynn.papertrail.controller;

import com.lynn.papertrail.dto.PaperSearchRequest;
import com.lynn.papertrail.dto.PaperSearchResponse;
import com.lynn.papertrail.entity.Paper;
import com.lynn.papertrail.service.PaperSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * 论文搜索控制器 - 支持多数据源
 * @author lynn
 */
@Slf4j
@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
@Validated
public class PaperController {

    private final PaperSearchService paperSearchService;

    /**
     * 搜索论文
     */
    @GetMapping("/search")
    public ResponseEntity<PaperSearchResponse> searchPapers(@Valid @ModelAttribute PaperSearchRequest request) {
        log.info("收到论文搜索请求: {}", request);

        try {
            PaperSearchResponse response = paperSearchService.searchPapers(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("搜索论文失败", e);
            return ResponseEntity.internalServerError()
                    .body(new PaperSearchResponse(null, 0, request.getStart(), 0, request.getQuery()));
        }
    }

    /**
     * POST方式搜索论文（支持复杂查询参数）
     */
    @PostMapping("/search")
    public ResponseEntity<PaperSearchResponse> searchPapersPost(@Valid @RequestBody PaperSearchRequest request) {
        log.info("收到POST论文搜索请求: {}", request);

        try {
            PaperSearchResponse response = paperSearchService.searchPapers(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("搜索论文失败", e);
            return ResponseEntity.internalServerError()
                    .body(new PaperSearchResponse(null, 0, request.getStart(), 0, request.getQuery()));
        }
    }

    /**
     * 根据ID获取论文详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Paper> getPaperById(@PathVariable String id) {
        log.info("获取论文详情，ID: {}", id);

        try {
            Paper paper = paperSearchService.getPaperById(id);
            if (paper != null) {
                return ResponseEntity.ok(paper);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取论文详情失败，ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取可用的数据源列表
     */
    @GetMapping("/sources")
    public ResponseEntity<Set<Paper.PaperSource>> getAvailableDataSources() {
        try {
            Set<Paper.PaperSource> sources = paperSearchService.getAvailableDataSources();
            return ResponseEntity.ok(sources);
        } catch (Exception e) {
            log.error("获取数据源列表失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取数据源健康状态
     */
    @GetMapping("/sources/health")
    public ResponseEntity<Map<String, Boolean>> getDataSourceHealth() {
        try {
            Map<String, Boolean> health = paperSearchService.getDataSourceHealth();
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("获取数据源健康状态失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
