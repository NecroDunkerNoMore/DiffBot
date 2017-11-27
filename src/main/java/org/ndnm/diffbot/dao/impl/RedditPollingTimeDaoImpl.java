package org.ndnm.diffbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.dao.RedditPollingTimeDao;
import org.ndnm.diffbot.model.RedditPollingTime;
import org.springframework.stereotype.Repository;


@Repository
public class RedditPollingTimeDaoImpl extends AbstractDao<BigInteger, RedditPollingTime> implements RedditPollingTimeDao {
    private static final String SELECT_BY_MAX_ID = "SELECT t FROM RedditPollingTime t where id = (select max(id) from RedditPollingTime where success = true)";

    @SuppressWarnings("unchecked")//getResultList()
    @Override
    public RedditPollingTime getLastRedditPollingTime() {
        List<RedditPollingTime> pollingTimes = getEntityManager()
                .createQuery(SELECT_BY_MAX_ID)
                .getResultList();

        if (pollingTimes == null || pollingTimes.size() == 0) {
            return null;
        }

        return pollingTimes.get(0);
    }


    @Override
    public RedditPollingTime findById(BigInteger id) {
        return super.getByKey(id);
    }


    @Override
    public void save(RedditPollingTime redditPollingTime) {
        persist(redditPollingTime);
    }


    @Override
    public void delete(RedditPollingTime redditPollingTime) {
        redditPollingTime = getEntityManager().contains(redditPollingTime) ? redditPollingTime : getEntityManager().merge(redditPollingTime);
        super.delete(redditPollingTime);
    }


    @Override
    public void update(RedditPollingTime redditPollingTime) {
        super.update(redditPollingTime);
    }

}
