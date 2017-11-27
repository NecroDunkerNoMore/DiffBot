package org.ndnm.diffbot.service;

import java.math.BigInteger;

import org.ndnm.diffbot.model.RedditPollingTime;

public interface RedditPollingTimeService {
    RedditPollingTime getLastPollingTime();

    RedditPollingTime findById(BigInteger id);

    void save(RedditPollingTime urlPollingTime);

    void delete(RedditPollingTime urlPollingTime);

    void update(RedditPollingTime urlPollingTime);
}
