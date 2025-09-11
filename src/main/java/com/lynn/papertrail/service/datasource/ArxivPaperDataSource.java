package com.lynn.papertrail.service.datasource;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lynn.papertrail.dto.PaperSearchRequest;
import com.lynn.papertrail.dto.PaperSearchResponse;
import com.lynn.papertrail.dto.arxiv.ArxivFeed;
import com.lynn.papertrail.dto.arxiv.ArxivEntry;
import com.lynn.papertrail.entity.Paper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * arXiv论文数据源实现
 * @author lynn
 */
@Slf4j
@Service
public class ArxivPaperDataSource extends AbstractPaperDataSource {

    private static final String ARXIV_API_BASE_URL = "https://export.arxiv.org/api/query";

    private final XmlMapper xmlMapper;

    public ArxivPaperDataSource(WebClient webClient, XmlMapper xmlMapper) {
        super(webClient);
        this.xmlMapper = xmlMapper;
    }

    @Override
    public Paper.PaperSource getSourceType() {
        return Paper.PaperSource.ARXIV;
    }

    @Override
    public String getSourceName() {
        return "arXiv";
    }

    @Override
    public int getPriority() {
        return 1; // arXiv作为主要数据源，优先级最高
    }

    @Override
    protected PaperSearchResponse doSearchPapers(PaperSearchRequest request) {
        String searchQuery = buildSearchQuery(request);

        String url = UriComponentsBuilder.fromHttpUrl(ARXIV_API_BASE_URL)
                .queryParam("search_query", searchQuery)
                .queryParam("start", request.getStart())
                .queryParam("max_results", request.getMaxResults())
                .queryParam("sortBy", request.getSortBy())
                .queryParam("sortOrder", request.getSortOrder())
                .build()
                .toUriString();

        String xmlResponse = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (xmlResponse == null || xmlResponse.isEmpty()) {
            return new PaperSearchResponse(Collections.emptyList(), 0, request.getStart(), 0, request.getQuery());
        }

        try {
            ArxivFeed feed = xmlMapper.readValue(xmlResponse, ArxivFeed.class);
            if (feed == null) {
                return new PaperSearchResponse(Collections.emptyList(), 0, request.getStart(), 0, request.getQuery());
            }

            List<Paper> papers = convertToPapers(feed.getEntries());

            return new PaperSearchResponse(
                    papers,
                    feed.getTotalResults(),
                    feed.getStartIndex(),
                    feed.getItemsPerPage(),
                    request.getQuery()
            );
        } catch (Exception e) {
            log.error("解析arXiv XML响应时发生错误", e);
            return new PaperSearchResponse(Collections.emptyList(), 0, request.getStart(), 0, request.getQuery());
        }
    }

    @Override
    protected Paper doGetPaperById(String id) {
        // 如果是复合ID，提取arXiv ID部分
        String arxivId = id.startsWith("arxiv_") ? id.substring(6) : id;

        PaperSearchRequest request = new PaperSearchRequest();
        request.setQuery(arxivId);
        request.setSearchField("id");
        request.setMaxResults(1);

        PaperSearchResponse response = doSearchPapers(request);
        return response.getPapers().isEmpty() ? null : response.getPapers().get(0);
    }

    @Override
    protected boolean doHealthCheck() {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(ARXIV_API_BASE_URL)
                    .queryParam("search_query", "quantum")
                    .queryParam("max_results", 1)
                    .build()
                    .toUriString();

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response != null && response.contains("<feed");
        } catch (Exception e) {
            return false;
        }
    }

    private String buildSearchQuery(PaperSearchRequest request) {
        String query = request.getQuery().trim();
        String searchField = request.getSearchField();

        return switch (searchField) {
            case "title" -> "ti:" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            case "author" -> "au:" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            case "abstract" -> "abs:" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            case "comment" -> "co:" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            case "journal" -> "jr:" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            case "subject" -> "cat:" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            case "report" -> "rn:" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            case "id" -> "id:" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            default -> URLEncoder.encode(query, StandardCharsets.UTF_8);
        };
    }

    private List<Paper> convertToPapers(List<ArxivEntry> entries) {
        if (entries == null) {
            return Collections.emptyList();
        }

        return entries.stream()
                .map(this::convertToPaper)
                .collect(Collectors.toList());
    }

    private Paper convertToPaper(ArxivEntry entry) {
        String sourceId = extractArxivId(entry.getId());

        return Paper.builder()
                .id(buildPaperId(sourceId))
                .source(Paper.PaperSource.ARXIV)
                .sourceId(sourceId)
                .title(cleanText(entry.getTitle()))
                .summary(cleanText(entry.getSummary()))
                .authors(entry.getAuthors() != null ?
                    entry.getAuthors().stream()
                        .map(ArxivEntry.ArxivAuthor::getName)
                        .collect(Collectors.toList()) : Collections.emptyList())
                .publishedDate(parseDate(entry.getPublished()))
                .updatedDate(parseDate(entry.getUpdated()))
                .categories(entry.getCategories() != null ?
                    entry.getCategories().stream()
                        .map(ArxivEntry.ArxivCategory::getTerm)
                        .collect(Collectors.toList()) : Collections.emptyList())
                .primaryCategory(entry.getPrimaryCategory() != null ?
                    entry.getPrimaryCategory().getTerm() : null)
                .pdfUrl(extractPdfUrl(entry.getLinks()))
                .paperUrl(extractPaperUrl(entry.getLinks()))
                .extraProperties(buildExtraProperties(entry))
                .build();
    }

    private String extractArxivId(String fullId) {
        if (fullId == null) return null;
        String[] parts = fullId.split("/");
        return parts[parts.length - 1];
    }

    private String cleanText(String text) {
        return text != null ? text.replaceAll("\\s+", " ").trim() : null;
    }

    private LocalDateTime parseDate(String dateStr) {
        try {
            return java.time.OffsetDateTime.parse(dateStr).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractPdfUrl(List<ArxivEntry.ArxivLink> links) {
        return links != null ? links.stream()
                .filter(link -> "related".equals(link.getRel()) && "pdf".equals(link.getTitle()))
                .map(ArxivEntry.ArxivLink::getHref)
                .findFirst()
                .orElse(null) : null;
    }

    private String extractPaperUrl(List<ArxivEntry.ArxivLink> links) {
        return links != null ? links.stream()
                .filter(link -> "alternate".equals(link.getRel()))
                .map(ArxivEntry.ArxivLink::getHref)
                .findFirst()
                .orElse(null) : null;
    }

    private HashMap<String, Object> buildExtraProperties(ArxivEntry entry) {
        HashMap<String, Object> extra = new HashMap<>();
        if (entry.getLinks() != null) {
            extra.put("links", entry.getLinks());
        }
        return extra;
    }
}
