package com.zrlog.plugin.sitemap.vo;

public class SiteMapResultInfo {

    private final String content;
    private final String version;

    public SiteMapResultInfo(String content, String version) {
        this.content = content;
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public String getVersion() {
        return version;
    }
}
