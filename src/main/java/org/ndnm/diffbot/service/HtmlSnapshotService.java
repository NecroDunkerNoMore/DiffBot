package org.ndnm.diffbot.service;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.model.diff.HtmlSnapshot;

public interface HtmlSnapshotService {
    List<HtmlSnapshot> findAll();

    HtmlSnapshot findLatest(DiffUrl diffUrl);

    HtmlSnapshot findById(BigInteger id);

    void save(HtmlSnapshot htmlSnapshot);

    void delete(HtmlSnapshot htmlSnapshot);

    void update(HtmlSnapshot htmlSnapshot);
}
