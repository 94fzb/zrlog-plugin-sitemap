package com.zrlog.plugin.sitemap.controller;

import com.google.gson.Gson;
import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.common.IdUtil;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.data.codec.HttpRequestInfo;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.render.SimpleTemplateRender;
import com.zrlog.plugin.sitemap.Application;
import com.zrlog.plugin.sitemap.handle.AutoRefreshSiteMapFileRunnable;
import com.zrlog.plugin.type.ActionType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SiteMapController {


    private final IOSession session;
    private final MsgPacket requestPacket;
    private final HttpRequestInfo requestInfo;

    public SiteMapController(IOSession session, MsgPacket requestPacket, HttpRequestInfo requestInfo) {
        this.session = session;
        this.requestPacket = requestPacket;
        this.requestInfo = requestInfo;
    }

    public void update() {
        session.sendMsg(new MsgPacket(requestInfo.simpleParam(), ContentType.JSON, MsgPacketStatus.SEND_REQUEST, IdUtil.getInt(), ActionType.SET_WEBSITE.name()), msgPacket -> {
            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            session.sendMsg(new MsgPacket(map, ContentType.JSON, MsgPacketStatus.RESPONSE_SUCCESS, requestPacket.getMsgId(), requestPacket.getMethodStr()));
            Application.getAutoRefreshSiteMapFile().doFeed();
            //更新缓存
            session.sendJsonMsg(new HashMap<>(), ActionType.REFRESH_CACHE.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST);
        });
    }

    public void index() {
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", "uriPath,sitemapText");
        session.sendJsonMsg(keyMap, ActionType.GET_WEBSITE.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST, msgPacket -> {
            Map map = new Gson().fromJson(msgPacket.getDataStr(), Map.class);
            Map<String, Object> data = new HashMap<>();
            data.put("theme", Objects.equals(requestInfo.getHeader().get("Dark-Mode"), "true") ? "dark" : "light");
            if (Objects.isNull(map.get("uriPath"))) {
                map.put("uriPath", AutoRefreshSiteMapFileRunnable.DEFAULT_URI_PATH);
            }
            data.put("data", new Gson().toJson(map));
            session.responseHtmlStr(new SimpleTemplateRender().render("/templates/index.html", session.getPlugin(), data), requestPacket.getMethodStr(), requestPacket.getMsgId());
        });
    }

    public void widget() {
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", "uriPath,sitemapText");
        session.sendJsonMsg(keyMap, ActionType.GET_WEBSITE.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST, msgPacket -> {
            Map map = new Gson().fromJson(msgPacket.getDataStr(), Map.class);
            if (Objects.isNull(map.get("uriPath"))) {
                map.put("uriPath", AutoRefreshSiteMapFileRunnable.DEFAULT_URI_PATH);
            }
            if (Objects.isNull(map.get("sitemapText"))) {
                map.put("sitemapText", "");
            }
            map.put("target", requestInfo.simpleParam().containsKey("preview") ? "_blank" : "_top");
            session.responseHtmlStr(new SimpleTemplateRender().render("/templates/widget.html", session.getPlugin(), map), requestPacket.getMethodStr(), requestPacket.getMsgId());
        });
    }

    public void xml() {
        session.responseXmlStr(Application.getAutoRefreshSiteMapFile().doFeed(), requestPacket.getMethodStr(), requestPacket.getMsgId());
    }
}