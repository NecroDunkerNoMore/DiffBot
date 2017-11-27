package org.ndnm.diffbot.service;

import java.math.BigInteger;

import org.ndnm.diffbot.model.AuthPollingTime;

public interface AuthPollingTimeService {

    AuthPollingTime getLastSuccessfulAuth();

    AuthPollingTime findById(BigInteger id);

    void save(AuthPollingTime authPollingTime);

    void delete(AuthPollingTime authPollingTime);

    void update(AuthPollingTime authPollingTime);

}
