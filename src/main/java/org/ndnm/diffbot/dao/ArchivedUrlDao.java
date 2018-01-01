package org.ndnm.diffbot.dao;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.model.ArchivedUrl;

public interface ArchivedUrlDao {
    List<ArchivedUrl> findAll();
    List<ArchivedUrl> findAllByDiffUrlId(BigInteger diffUrlId);
    ArchivedUrl findById(BigInteger id);
    void save(ArchivedUrl archivedUrl);
    void delete(ArchivedUrl archivedUrl);
    void update(ArchivedUrl archivedUrl);
}
