package org.ndnm.diffbot.dao;

import java.math.BigInteger;

import org.ndnm.diffbot.model.RedditPollingTime;


public interface RedditTimeServiceDao {
    RedditPollingTime getLastRedditPollingTime();

    RedditPollingTime findById(BigInteger id);

    void save(RedditPollingTime redditPollingTime);

    void delete(RedditPollingTime redditPollingTime);

    void update(RedditPollingTime redditPollingTime);
}
