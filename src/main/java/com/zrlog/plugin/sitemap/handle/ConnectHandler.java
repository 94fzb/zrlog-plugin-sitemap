package com.zrlog.plugin.sitemap.handle;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.RunConstants;
import com.zrlog.plugin.api.IConnectHandler;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.type.RunType;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectHandler implements IConnectHandler {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private AutoRefreshSiteMapFileRunnable autoRefreshSiteMapFileRunnable;

    @Override
    public void handler(IOSession ioSession, MsgPacket msgPacket) {
        this.autoRefreshSiteMapFileRunnable = new AutoRefreshSiteMapFileRunnable(ioSession);
        executorService.scheduleAtFixedRate(autoRefreshSiteMapFileRunnable, 0, 2, RunConstants.runType == RunType.BLOG ? TimeUnit.MINUTES : TimeUnit.HOURS);
    }

    public AutoRefreshSiteMapFileRunnable getAutoRefreshFeedFile() {
        return autoRefreshSiteMapFileRunnable;
    }
}