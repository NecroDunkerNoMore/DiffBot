package org.ndnm.diffbot.service;

import java.math.BigInteger;

import org.ndnm.diffbot.model.diff.DiffResult;

public interface DiffResultService {

    DiffResult findById(BigInteger id);

    void save(DiffResult diffResult);

    void update(DiffResult diffResult);

    void delete(DiffResult diffResult);
}
