package org.ndnm.diffbot.dao;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.model.diff.HtmlSnapshot;

public interface HtmlSnapshotDao {
    List<HtmlSnapshot> findAll();

    HtmlSnapshot findById(BigInteger id);

    void save(HtmlSnapshot htmlSnapshot);

    void delete(HtmlSnapshot htmlSnapshot);

    void update(HtmlSnapshot htmlSnapshot);
}
