package org.ndnm.diffbot.dao;

import java.math.BigInteger;
import java.util.List;

public interface DiffDeltaDao {
    List<DiffDeltaDao> findAll();

    DiffDeltaDao findById(BigInteger id);

    void save(DiffDeltaDao stashUrl);

    void delete(DiffDeltaDao stashUrl);

    void update(DiffDeltaDao stashUrl);
}
