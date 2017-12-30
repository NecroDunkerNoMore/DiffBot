package org.ndnm.diffbot.service;

import org.ndnm.diffbot.model.AuthPollingTime;

public interface TimingService {
    long getAuthSleepIntervalInMillis();

    long getDiffPollingIntervalInMillis();

    long getRedditPollingIntervalInMillis();

    long getOauthRefreshIntervalInMillis();

    long getMainLoopIntervalInMillis();

    int getMaxAuthAttempts();

    void saveAuthPollingTime(AuthPollingTime time);

    AuthPollingTime getLastSuccessfulAuth();

    boolean isTimeToRefreshAuth();

    boolean isTimeToCheckRedditMail();

    boolean isTimeToProcessDiffUrls();
}
