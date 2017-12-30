package org.ndnm.diffbot.service;

public interface TimingService {
    long getAuthSleepIntervalInMillis();

    long getDiffPollingIntervalInMillis();

    long getRedditPollingIntervalInMillis();

    long getOauthRefreshIntervalInMillis();

    long getMainLoopIntervalInMillis();

    int getMaxAuthAttempts();
}
