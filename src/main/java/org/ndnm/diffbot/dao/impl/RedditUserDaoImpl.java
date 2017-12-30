package org.ndnm.diffbot.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.ndnm.diffbot.dao.RedditUserDao;
import org.ndnm.diffbot.model.RedditUser;
import org.springframework.stereotype.Repository;


@Repository
public class RedditUserDaoImpl extends AbstractDao<BigInteger, RedditUser> implements RedditUserDao {
    private static final String SELECT_BY_USERNAME = "SELECT u FROM RedditUser u where username = :username";
    private static final String SELECT_BY_ALL_NON_BLACKLISTED_SUBSCRIBERS = "select u from RedditUser u where subscribed = true and blacklisted = false";


    @Override
    public boolean isUserBlacklisted(String username) {
        RedditUser user = getRedditUserbyUsername(username);
        return user != null && user.isBlacklisted();
    }


    @Override
    public RedditUser findById(BigInteger id) {
        return super.getByKey(id);
    }


    @Override
    public void save(RedditUser user) {
        super.persist(user);
    }


    @Override
    public void delete(RedditUser user) {
        user = getEntityManager().contains(user) ? user : getEntityManager().merge(user);
        super.delete(user);
    }


    @Override
    public void update(RedditUser user) {
        super.update(user);
    }


    @SuppressWarnings("unchecked")//getResultList()
    @Override
    public RedditUser getRedditUserbyUsername(String username) {
        List<RedditUser> blacklistedUsers = getEntityManager()
                .createQuery(SELECT_BY_USERNAME)
                .setParameter("username", username)
                .getResultList();

        if (blacklistedUsers == null || blacklistedUsers.size() == 0) {
            return null;
        }

        return blacklistedUsers.get(0);
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<RedditUser> getAllNonBlacklistedSubscribers() {
        List<RedditUser> subscribers = getEntityManager()
                .createQuery(SELECT_BY_ALL_NON_BLACKLISTED_SUBSCRIBERS)
                .getResultList();

        if (subscribers == null || subscribers.size() == 0) {
            return new ArrayList<>();
        }

        return subscribers;
    }

}
