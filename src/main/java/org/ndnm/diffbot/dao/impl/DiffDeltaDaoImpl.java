package org.ndnm.diffbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.dao.DiffDeltaDao;
import org.ndnm.diffbot.model.diff.DiffDelta;
import org.springframework.stereotype.Repository;


@Repository
public class DiffDeltaDaoImpl extends AbstractDao<BigInteger, DiffDelta> implements DiffDeltaDao {
    @Override
    public List<DiffDeltaDao> findAll() {
        return null;
    }


    @Override
    public DiffDeltaDao findById(BigInteger id) {
        return null;
    }


    @Override
    public void save(DiffDeltaDao stashUrl) {

    }


    @Override
    public void delete(DiffDeltaDao stashUrl) {

    }


    @Override
    public void update(DiffDeltaDao stashUrl) {

    }
}
