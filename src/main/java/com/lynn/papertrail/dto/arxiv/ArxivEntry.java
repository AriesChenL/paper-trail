package com.lynn.papertrail.dto.arxiv;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

/**
 * arXiv论文条目
 * @author lynn
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArxivEntry {

    @JacksonXmlProperty(localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "title")
    private String title;

    @JacksonXmlProperty(localName = "summary")
    private String summary;

    @JacksonXmlProperty(localName = "published")
    private String published;

    @JacksonXmlProperty(localName = "updated")
    private String updated;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "author")
    private List<ArxivAuthor> authors;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "category")
    private List<ArxivCategory> categories;

    @JacksonXmlProperty(localName = "primary_category")
    private ArxivCategory primaryCategory;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    private List<ArxivLink> links;

    /**
     * 作者信息
     */
    @Data
    public static class ArxivAuthor {
        @JacksonXmlProperty(localName = "name")
        private String name;
    }

    /**
     * 分类信息
     */
    @Data
    public static class ArxivCategory {
        @JacksonXmlProperty(isAttribute = true, localName = "term")
        private String term;

        @JacksonXmlProperty(isAttribute = true, localName = "scheme")
        private String scheme;
    }

    /**
     * 链接信息
     */
    @Data
    public static class ArxivLink {
        @JacksonXmlProperty(isAttribute = true, localName = "href")
        private String href;

        @JacksonXmlProperty(isAttribute = true, localName = "rel")
        private String rel;

        @JacksonXmlProperty(isAttribute = true, localName = "type")
        private String type;

        @JacksonXmlProperty(isAttribute = true, localName = "title")
        private String title;
    }
}
