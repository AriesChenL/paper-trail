package com.lynn.papertrail.dto.arxiv;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

/**
 * arXiv API响应根元素
 * @author lynn
 */
@Data
@JacksonXmlRootElement(localName = "feed")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArxivFeed {

    @JacksonXmlProperty(localName = "title")
    private String title;

    @JacksonXmlProperty(localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "updated")
    private String updated;

    @JacksonXmlProperty(localName = "link")
    private ArxivLink link;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "entry")
    private List<ArxivEntry> entries;

    @JacksonXmlProperty(localName = "totalResults", namespace = "http://a9.com/-/spec/opensearch/1.1/")
    private int totalResults;

    @JacksonXmlProperty(localName = "startIndex", namespace = "http://a9.com/-/spec/opensearch/1.1/")
    private int startIndex;

    @JacksonXmlProperty(localName = "itemsPerPage", namespace = "http://a9.com/-/spec/opensearch/1.1/")
    private int itemsPerPage;

    /**
     * 链接信息
     */
    @Data
    public static class ArxivLink {
        @JacksonXmlProperty(isAttribute = true)
        private String href;

        @JacksonXmlProperty(isAttribute = true)
        private String rel;

        @JacksonXmlProperty(isAttribute = true)
        private String type;
    }
}
