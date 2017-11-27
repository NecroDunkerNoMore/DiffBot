package org.ndnm.diffbot.dao;

import java.math.BigInteger;

import org.ndnm.diffbot.model.UrlPollingTime;

public interface UrlPollingTimeDao {
    UrlPollingTime getUrlPollingTime();

    UrlPollingTime findById(BigInteger id);

    void save(UrlPollingTime urlPollingTime);

    void delete(UrlPollingTime urlPollingTime);

    void update(UrlPollingTime urlPollingTime);
}
