package org.ndnm.diffbot.service;

import java.math.BigInteger;

import org.ndnm.diffbot.model.PollingTime;
import org.ndnm.diffbot.model.UrlPollingTime;

public interface PollingTimeService<T extends PollingTime> {
    T getLastPollingTime();

    T findById(BigInteger id);

    void save(UrlPollingTime urlPollingTime);

    void delete(UrlPollingTime urlPollingTime);

    void update(UrlPollingTime urlPollingTime);
}
