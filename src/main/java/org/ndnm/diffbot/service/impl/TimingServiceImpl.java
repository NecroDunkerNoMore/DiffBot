package org.ndnm.diffbot.service.impl;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndnm.diffbot.model.AuthPollingTime;
import org.ndnm.diffbot.model.RedditPollingTime;
import org.ndnm.diffbot.model.UrlPollingTime;
import org.ndnm.diffbot.service.AuthPollingTimeService;
import org.ndnm.diffbot.service.RedditPollingTimeService;
import org.ndnm.diffbot.service.TimingService;
import org.ndnm.diffbot.service.UrlPollingTimeService;
import org.ndnm.diffbot.util.TimeUtils;
import org.springframework.stereotype.Component;


@Component
public class TimingServiceImpl implements TimingService {
    private static final Logger LOG = LogManager.getLogger(TimingServiceImpl.class);

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
        AuthPollingTime lastAuthTime = getAuthPollingTimeService().getLastSuccessfulAuth();
        long now = TimeUtils.getTimeGmt().getTime();
        long lastAuth = lastAuthTime.getDate().getTime();

        return (now - lastAuth) >= oauthRefreshIntervalInMillis;
    }


    @Override
    public boolean isTimeToCheckRedditMail() {
        long lastPollTime = getRedditPollingTimeService().getLastPollingTime().getDate().getTime();
        long now = TimeUtils.getTimeGmt().getTime();

        return (now - lastPollTime) >= redditPollingIntervalInMillis;
    }


    @Override
    public boolean isTimeToProcessDiffUrls() {
        long lastSuccessfulPollTime = getUrlPollingTimeService().getLastPollingTime().getDate().getTime();
        long now = TimeUtils.getTimeGmt().getTime();
        long timeBetweenNowAndLastUrlPoll = now - lastSuccessfulPollTime;

        if (timeBetweenNowAndLastUrlPoll < diffPollingIntervalInMillis) {
            long secondsToGo = (diffPollingIntervalInMillis - timeBetweenNowAndLastUrlPoll) / 1000;
            LOG.info("%d seconds till next URL polling.", secondsToGo);
        }

        return timeBetweenNowAndLastUrlPoll >= diffPollingIntervalInMillis;
    }


    @Override
    public void saveNewAuthPollingTime(boolean success) {
        AuthPollingTime time = new AuthPollingTime();
        time.setDate(TimeUtils.getTimeGmt());
        time.setSuccess(success);

        getAuthPollingTimeService().save(time);
    }


    @Override
    public void saveNewUrlPollingTime(boolean success) {
        UrlPollingTime time = new UrlPollingTime();
        time.setDate(TimeUtils.getTimeGmt());
        time.setSuccess(success);

        getUrlPollingTimeService().save(time);
    }


    @Override
    public void saveNewRedditPollingTime(boolean success) {
        RedditPollingTime time = new RedditPollingTime();
        time.setDate(TimeUtils.getTimeGmt());
        time.setSuccess(success);

        getRedditPollingTimeService().save(time);
    }


    @Override
    public long getMainLoopIntervalInMillis() {
        return mainLoopIntervalInMillis;
    }


    @Override
    public long getAuthSleepIntervalInMillis() {
        return authSleepIntervalInMillis;
    }


    private RedditPollingTimeService getRedditPollingTimeService() {
        return redditPollingTimeService;
    }


    private AuthPollingTimeService getAuthPollingTimeService() {
        return authPollingTimeService;
    }


    private UrlPollingTimeService getUrlPollingTimeService() {
        return urlPollingTimeService;
    }
}
