package org.ndnm.diffbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.dao.UrlPollingTimeServiceDao;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.model.UrlPollingTime;
import org.springframework.stereotype.Repository;


@Repository
public class UrlPollingTimeServiceDaoImpl extends AbstractDao<BigInteger, UrlPollingTime> implements UrlPollingTimeServiceDao {
    private static final String TARGET_URL_PARAM = "targetUrl";
    private static final String SELECT_BY_LATEST_URL = String.format("select t from UrlPollingTime t " +
                                                                       "where t.date = " +
                                                                          "(select max(t.date) from UrlPollingTime " +
                                                                             "where t.url = :%s)", TARGET_URL_PARAM);

    @SuppressWarnings("unchecked")//getResultList()
    @Override
    public UrlPollingTime getUrlPollingTime(DiffUrl diffUrl) {
        List<UrlPollingTime> pollingTimes = getEntityManager()
                .createQuery(SELECT_BY_LATEST_URL)
                .setParameter(TARGET_URL_PARAM, diffUrl.getSourceUrl())
                .getResultList();

        if (pollingTimes == null || pollingTimes.size() == 0) {
            return null;
        }

        return pollingTimes.get(0);
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
