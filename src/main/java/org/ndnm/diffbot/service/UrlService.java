package org.ndnm.diffbot.service;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.model.DiffUrl;

public interface UrlService {
    List<DiffUrl> findAll();

    DiffUrl findById(BigInteger id);

    void save(DiffUrl diffUrl);

    void delete(DiffUrl diffUrl);

    void update(DiffUrl diffUrl);
}
