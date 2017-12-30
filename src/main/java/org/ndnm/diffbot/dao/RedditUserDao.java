package org.ndnm.diffbot.dao;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.model.RedditUser;

public interface RedditUserDao {
    RedditUser getRedditUserbyUsername(String username);

    boolean isUserBlacklisted(String username);

    RedditUser findById(BigInteger id);

    void save(RedditUser user);

    void delete(RedditUser redditUser);

    void update(RedditUser redditUser);

    List<RedditUser> getAllNonBlacklistedSubscribers();
}
