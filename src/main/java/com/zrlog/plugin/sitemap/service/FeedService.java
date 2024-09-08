package com.zrlog.plugin.sitemap.service;

import com.google.gson.Gson;
import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.common.model.PublicInfo;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.sitemap.vo.Article;
import com.zrlog.plugin.sitemap.vo.SiteMapResultInfo;
import com.zrlog.plugin.type.ActionType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class FeedService {

    private final IOSession session;

    public FeedService(IOSession session) {
        this.session = session;
    }

    public SiteMapResultInfo feed() {
        PublicInfo publicInfo = session.getResponseSync(ContentType.JSON, new HashMap<>(), ActionType.LOAD_PUBLIC_INFO, PublicInfo.class);
        try {
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(publicInfo.getApiHomeUrl() + "/api/article?size=50000&feed=true")).build();
            HttpResponse<byte[]> send = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            Map<String, Object> info = new Gson().fromJson(new String(send.body()), Map.class);
            Map<String, Object> data = (Map<String, Object>) info.get("data");
            List<Map<String, Object>> rows = (List<Map<String, Object>>) data.get("rows");
            List<Article> articles = new ArrayList<>();
            rows.forEach(e -> {
                String pubDate = (String) e.get("releaseTime");
                articles.add(new Article((String) e.get("title"), "https:" + e.get("url"),
                        Objects.requireNonNullElse((String) e.get("content"), ""), pubDate, ((Double) e.get("id")).longValue() + ""));
            });
            //httpClient.close();
            return SiteMapGenerator.generateSitemap(publicInfo.getTitle(), publicInfo.getHomeUrl(), "", articles);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
