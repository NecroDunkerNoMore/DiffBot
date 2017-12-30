package org.ndnm.diffbot.service.impl;

import javax.annotation.Resource;

import org.ndnm.diffbot.service.TimingService;
import org.springframework.stereotype.Component;


@Component
public class TimingServiceImpl implements TimingService {
    @Resource(name = "authSleepIntervalInMillis")
    private long authSleepIntervalInMillis;

    @Resource(name = "diffPollingIntervalInMillis")
    private long diffPollingIntervalInMillis;

    @Resource(name = "redditPollingIntervalInMillis")
    private long redditPollingIntervalInMillis;

    @Resource(name = "oauthRefreshIntervalInMillis")
    private long oauthRefreshIntervalInMillis;

    @Resource(name = "mainLoopIntervalInMillis")
    private long mainLoopIntervalInMillis;

    @Resource(name = "maxAuthAttempts")
    private int maxAuthAttempts;


    @Override
    public long getAuthSleepIntervalInMillis() {
        return authSleepIntervalInMillis;
    }


    @Override
    public long getDiffPollingIntervalInMillis() {
        return diffPollingIntervalInMillis;
    }


    @Override
    public long getRedditPollingIntervalInMillis() {
        return redditPollingIntervalInMillis;
    }


    @Override
    public long getOauthRefreshIntervalInMillis() {
        return oauthRefreshIntervalInMillis;
    }


    @Override
    public long getMainLoopIntervalInMillis() {
        return mainLoopIntervalInMillis;
    }


    @Override
    public int getMaxAuthAttempts() {
        return maxAuthAttempts;
    }

}
