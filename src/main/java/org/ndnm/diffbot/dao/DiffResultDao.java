package org.ndnm.diffbot.dao;

import java.math.BigInteger;

import org.ndnm.diffbot.model.diff.DiffResult;

public interface DiffResultDao {

    DiffResult findById(BigInteger id);

    void save(DiffResult diffResult);

    void delete(DiffResult diffResult);

    DiffResult findByTargetCommentId(String targetCommentId);

    boolean existsByTargetCommentId(String targetCommentId);

}
