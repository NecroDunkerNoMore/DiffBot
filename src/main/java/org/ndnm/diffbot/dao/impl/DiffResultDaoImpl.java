package org.ndnm.diffbot.dao.impl;

import java.math.BigInteger;

import org.ndnm.diffbot.dao.DiffResultDao;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.springframework.stereotype.Repository;


@Repository
public class DiffResultDaoImpl extends AbstractDao<BigInteger, DiffResult> implements DiffResultDao {

    @Override
    public DiffResult findById(BigInteger id) {
        return getByKey(id);
    }


    @Override
    public void save(DiffResult diffResult) {
        persist(diffResult);
    }


    @Override
    public void update(DiffResult diffResult) {
        super.update(diffResult);
    }


    @Override
    public void delete(DiffResult diffResult) {
        diffResult = getEntityManager().contains(diffResult) ? diffResult : getEntityManager().merge(diffResult);
        super.delete(diffResult);
    }

}
