package org.ndnm.diffbot.service;

public interface TimingService {

    void saveNewAuthPollingTime(boolean success);

    void saveNewUrlPollingTime(boolean success);

    void saveNewRedditPollingTime(boolean success);

    boolean isTimeToRefreshAuth();

    boolean isTimeToCheckRedditMail();

    boolean isTimeToProcessDiffUrls();

    long getMainLoopIntervalInMillis();

    long getAuthSleepIntervalInMillis();
}
