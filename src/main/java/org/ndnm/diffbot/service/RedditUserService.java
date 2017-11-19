package org.ndnm.diffbot.service;

import java.math.BigInteger;

import org.ndnm.diffbot.model.RedditUser;


public interface RedditUserService {
    RedditUser getRedditUserbyUsername(String username);

    RedditUser findById(BigInteger id);

    void save(RedditUser redditUser);

    void delete(RedditUser redditUser);

    void update(RedditUser redditUser);

    boolean isUserBlacklisted(String username);
}
