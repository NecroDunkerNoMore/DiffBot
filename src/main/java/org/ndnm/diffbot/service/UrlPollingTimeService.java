package org.ndnm.diffbot.service;

import java.math.BigInteger;

import org.ndnm.diffbot.model.UrlPollingTime;

public interface UrlPollingTimeService extends PollingTimeService<UrlPollingTime>{
    UrlPollingTime getLastPollingTime();

    UrlPollingTime findById(BigInteger id);

    void save(UrlPollingTime urlPollingTime);

    void delete(UrlPollingTime urlPollingTime);

    void update(UrlPollingTime urlPollingTime);
}
