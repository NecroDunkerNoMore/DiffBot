package org.ndnm.diffbot.dao;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.model.diff.DiffUrl;

public interface DiffUrlDao {
    List<DiffUrl> findAll();

    DiffUrl findById(BigInteger id);

    void save(DiffUrl diffUrl);

    void delete(DiffUrl diffUrl);

    void update(DiffUrl diffUrl);
}
