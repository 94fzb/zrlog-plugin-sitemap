package com.zrlog.plugin.sitemap.service;

import com.zrlog.plugin.common.SecurityUtils;
import com.zrlog.plugin.sitemap.vo.Article;
import com.zrlog.plugin.sitemap.vo.SiteMapResultInfo;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class SiteMapGenerator {

    public static void main(String[] args) {
        // Example usage
        List<Article> articles = Arrays.asList(
                new Article("Latest Article Title 1", "http://www.example.com/article/1", "This is a description of the latest article 1", ZonedDateTime.now().minusDays(1).format(DateTimeFormatter.RFC_1123_DATE_TIME), "1"),
                new Article("Latest Article Title 2", "http://www.example.com/article/2", "This is a description of the latest article 2", ZonedDateTime.now().minusDays(2).format(DateTimeFormatter.RFC_1123_DATE_TIME), "2")
                // Add more articles as needed
        );

        String sitemap = generateSitemap("Example sitemap Feed", "http://www.example.com", "This is an example of an sitemap feed", articles).getContent();
        System.out.println("sitemap = " + sitemap);
    }

    public static SiteMapResultInfo generateSitemap(String title, String link, String description, List<Article> articles) {
        String language = "zh-cn";

        // Create the sitemap feed content
        StringBuilder sitemapContent = new StringBuilder();
        sitemapContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        sitemapContent.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // Add articles from the list
        StringJoiner rawContent = new StringJoiner("\n");
        rawContent.add(title).add(link).add(language).add(description);
        for (Article article : articles) {
            sitemapContent.append("    <url>\n");
            sitemapContent.append("      <loc><![CDATA[").append(article.getLink()).append("]]></loc>\n");
            sitemapContent.append("      <lastmod><![CDATA[").append(article.getPubDate()).append("]]></lastmod>\n");
            sitemapContent.append("      <changefreq>").append("monthly").append("</changefreq>\n");
            sitemapContent.append("      <priority>").append("0.8").append("</priority>\n");
            sitemapContent.append("    </url>\n");
            //compare
            rawContent.add(article.getTitle());
            rawContent.add(article.getLink());
            rawContent.add(article.getDescription());
            rawContent.add(article.getPubDate());
            rawContent.add(article.getGuid());
        }

        sitemapContent.append("</urlset>\n");

        return new SiteMapResultInfo(sitemapContent.toString(), SecurityUtils.md5(rawContent.toString()));
    }
}