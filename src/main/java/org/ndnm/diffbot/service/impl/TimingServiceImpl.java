package org.ndnm.diffbot.service.impl;

import javax.annotation.Resource;

import org.ndnm.diffbot.model.AuthPollingTime;
import org.ndnm.diffbot.service.AuthPollingTimeService;
import org.ndnm.diffbot.service.RedditPollingTimeService;
import org.ndnm.diffbot.service.TimingService;
import org.ndnm.diffbot.service.UrlPollingTimeService;
import org.ndnm.diffbot.util.TimeUtils;
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

    private final RedditPollingTimeService redditPollingTimeService;
    private final AuthPollingTimeService authPollingTimeService;
    private final UrlPollingTimeService urlPollingTimeService;


    public TimingServiceImpl(RedditPollingTimeService redditPollingTimeService, AuthPollingTimeService authPollingTimeService, UrlPollingTimeService urlPollingTimeService) {
        this.redditPollingTimeService = redditPollingTimeService;
        this.authPollingTimeService = authPollingTimeService;
        this.urlPollingTimeService = urlPollingTimeService;
    }


    @Override
    public boolean isTimeToRefreshAuth() {
        AuthPollingTime lastAuthTime = getLastSuccessfulAuth();
        long now = TimeUtils.getTimeGmt().getTime();
        long lastAuth = lastAuthTime.getDate().getTime();

        return (now - lastAuth) >= getOauthRefreshIntervalInMillis();
    }


    @Override
    public boolean isTimeToCheckRedditMail() {
        long lastPollTime = getRedditPollingTimeService().getLastPollingTime().getDate().getTime();
        long now = TimeUtils.getTimeGmt().getTime();

        return (now - lastPollTime) >= getRedditPollingIntervalInMillis();
    }


    @Override
    public boolean isTimeToProcessDiffUrls() {
        long lastSuccessfulPollTime = getUrlPollingTimeService().getLastPollingTime().getDate().getTime();
        long now = TimeUtils.getTimeGmt().getTime();

        return (now - lastSuccessfulPollTime) >= getDiffPollingIntervalInMillis();
    }


    @Override
    public void saveAuthPollingTime(AuthPollingTime time) {
        getAuthPollingTimeService().save(time);
    }


    @Override
    public AuthPollingTime getLastSuccessfulAuth() {
        return getAuthPollingTimeService().getLastSuccessfulAuth();
    }


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


    public RedditPollingTimeService getRedditPollingTimeService() {
        return redditPollingTimeService;
    }


    public AuthPollingTimeService getAuthPollingTimeService() {
        return authPollingTimeService;
    }


    public UrlPollingTimeService getUrlPollingTimeService() {
        return urlPollingTimeService;
    }
}
