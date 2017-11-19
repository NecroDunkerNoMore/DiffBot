package org.ndnm.diffbot.dao;

import java.math.BigInteger;

import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.model.UrlPollingTime;

public interface UrlPollingTimeServiceDao {
    UrlPollingTime getUrlPollingTime(DiffUrl diffUrl);

    UrlPollingTime findById(BigInteger id);

    void save(UrlPollingTime urlPollingTime);

    void delete(UrlPollingTime urlPollingTime);

    void update(UrlPollingTime urlPollingTime);
}
