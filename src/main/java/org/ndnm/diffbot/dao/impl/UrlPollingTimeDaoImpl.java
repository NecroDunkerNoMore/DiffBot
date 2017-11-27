package org.ndnm.diffbot.dao.impl;

import java.math.BigInteger;

import org.ndnm.diffbot.dao.UrlPollingTimeDao;
import org.ndnm.diffbot.model.UrlPollingTime;
import org.springframework.stereotype.Repository;


@Repository
public class UrlPollingTimeDaoImpl extends AbstractDao<BigInteger, UrlPollingTime> implements UrlPollingTimeDao {
    private static final String SELECT_BY_LAST_SUCCESS =
            "select t from UrlPollingTime t where id = (select max(id) from UrlPollingTime where success = true)";


    @SuppressWarnings("unchecked")//getResultList()
    @Override
    public UrlPollingTime getUrlPollingTime() {
        return (UrlPollingTime) getEntityManager()
                .createQuery(SELECT_BY_LAST_SUCCESS)
                .getSingleResult();
    }


    @Override
    public UrlPollingTime findById(BigInteger id) {
        return super.getByKey(id);
    }


    @Override
    public void save(UrlPollingTime urlPollingTime) {
        persist(urlPollingTime);
    }


    @Override
    public void delete(UrlPollingTime urlPollingTime) {
        urlPollingTime = getEntityManager().contains(urlPollingTime) ? urlPollingTime : getEntityManager().merge(urlPollingTime);
        super.delete(urlPollingTime);
    }


    @Override
    public void update(UrlPollingTime urlPollingTime) {
        super.update(urlPollingTime);
    }

}
